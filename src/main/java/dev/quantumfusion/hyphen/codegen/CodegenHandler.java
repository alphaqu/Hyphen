package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.ClassDefiner;
import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.codegen.def.MethodDef;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.thr.HyphenException;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public class CodegenHandler<IO extends IOInterface, D> {
	// Settings
	private final boolean debug;
	public final Class<IO> ioClass;
	public final Class<D> dataClass;
	public final String self;
	// Options
	public final EnumMap<Options, Boolean> options;
	// Method Dedup
	@Nullable
	private final Map<MethodInfo, AtomicInteger> methodDedup;

	// other
	private final ClassWriter cw;
	private final ClassDefiner definer;

	public CodegenHandler(Class<IO> ioClass, Class<D> dataClass, boolean debug, String self, EnumMap<Options, Boolean> options, ClassDefiner definer) {
		this.ioClass = ioClass;
		this.dataClass = dataClass;
		this.debug = debug;
		this.self = self;
		this.options = options;
		this.definer = definer;
		this.methodDedup = this.options.get(Options.SHORT_METHOD_NAMES) ? new HashMap<>() : null;
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

		this.cw.visit(V16, ACC_PUBLIC | ACC_FINAL, this.self, null, GenUtil.internal(Object.class), new String[]{GenUtil.internal(HyphenSerializer.class)});

		try (var mh = new MethodHandler(this.cw.visitMethod(ACC_PUBLIC, "<init>", GenUtil.methodDesc(Void.TYPE), null, null), this.self, dataClass, ioClass, true)) {
			mh.visitIntInsn(ALOAD, 0);
			mh.callInst(INVOKESPECIAL, Object.class, "<init>", Void.TYPE);
			mh.op(RETURN);
		}

		if (options.get(Options.FAST_ALLOC)) {
			try (var mh = new MethodHandler(this.cw.visitMethod(ACC_STATIC, "<clinit>", GenUtil.methodDesc(Void.TYPE), null, null), this.self, dataClass, ioClass, false)) {
				mh.visitTypeInsn(NEW, this.self);
				mh.op(DUP);
				mh.visitMethodInsn(INVOKESPECIAL, this.self, "<init>", GenUtil.methodDesc(Void.TYPE), false);
				mh.visitFieldInsn(PUTSTATIC, ClassDefiner.class, "SERIALIZER", HyphenSerializer.class);
				mh.op(RETURN);
			}
		}
	}

	public MethodInfo createMethodInfo(Clazz clazz, String prefix, Class<?> returnClass, Class<?>... parameters) {
		return createMethodInfo(clazz, prefix, "", returnClass, parameters);
	}

	public MethodInfo createMethodInfo(Clazz clazz, String prefix, String suffix, Class<?> returnClass, Class<?>... parameters) {
		var info = new MethodInfo(GenUtil.makeSafe(prefix + clazz.toString() + suffix), returnClass, parameters);
		if (methodDedup != null)
			info.setName(GenUtil.hyphenShortMethodName(methodDedup.computeIfAbsent(info, info1 -> new AtomicInteger(0)).getAndIncrement()));
		return info;
	}

	public void writeMethods(Collection<MethodDef> methods) {
		for (MethodDef method : methods) {
			writeMethod(method, false);
		}
	}

	private void writeMethod(MethodDef def, boolean spark) {
		def.writeMethods(this, this::writeMethodInternal, spark);
	}

	private void writeMethodInternal(Clazz clazz, MethodInfo methodInfo, boolean spark, boolean synthetic, Consumer<MethodHandler> writer) {
		final Class<?>[] parameters = methodInfo.parameters;
		try (var mh = new MethodHandler(cw, methodInfo, self, dataClass, ioClass, options.get(Options.SHORT_VARIABLE_NAMES), spark, synthetic)) {
			for (int i = 0; i < parameters.length; i++) {
				mh.addVar(MethodHandler.getParamName(i) + (spark ? "raw" : ""), spark ? Object.class : parameters[i]);
			}
			mh.visitCode();
			if (spark) {
				for (int i = 0; i < parameters.length; i++) {
					var parameter = parameters[i];
					var varName = MethodHandler.getParamName(i);
					mh.addVar(varName, parameter); // add var
					// cast
					mh.varOp(ILOAD, varName + "raw");
					mh.typeOp(CHECKCAST, parameter);
					mh.varOp(ISTORE, varName);
				}
			}

			try {
				writer.accept(mh);
			} catch (Throwable thr) {
				throw HyphenException.thr("class", "-", clazz.getDefinedClass().getSimpleName(), thr);
			}
			mh.op(Type.getType(methodInfo.returnClass).getOpcode(IRETURN));
			mh.visitEnd();
		} catch (Throwable throwable) {
			throw HyphenException.thr("method", "-", methodInfo.getName(), throwable);
		}
	}

	public void setupSpark(MethodDef spark) {
		spark.getInfo.setName("get");
		spark.putInfo.setName("put");
		spark.measureInfo.setName("measure");
		writeMethod(spark, true);
	}

	@SuppressWarnings("unchecked")
	public synchronized <O extends HyphenSerializer<IO, D>> HyphenSerializer<IO, D> export(Path exportPath) {
		final byte[] bytes = cw.toByteArray();

		try {
			if (exportPath != null) {
				try {
					Files.write(exportPath, bytes, StandardOpenOption.CREATE);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			final Class<O> def = (Class<O>) definer.def(self, bytes);

			if (options.get(Options.FAST_ALLOC)) {
				return (HyphenSerializer<IO, D>) ClassDefiner.SERIALIZER;
			} else {
				try {
					var constructor = def.getConstructor();
					constructor.setAccessible(true);
					return constructor.newInstance();
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			}
		} catch (Throwable thr) {
			CheckClassAdapter.verify(new ClassReader(bytes), true, new PrintWriter(System.out));
			throw thr;
		}
	}


	public interface MethodWriter {
		void writeMethod(Clazz clazz, MethodInfo methodInfo, boolean spark, boolean synthetic, Consumer<MethodHandler> writer);
	}
}
