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
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.io.IOException;
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
	public final Class<IO> ioClass;
	public final Class<D> dataClass;
	public final String self;
	private final boolean debug;

	// Options
	public final EnumMap<Options, Boolean> options;

	// Method Dedup
	@Nullable
	private final Map<MethodInfo, AtomicInteger> methodDedup;

	// other
	private final ClassWriter cw;
	private final ClassDefiner definer;

	public CodegenHandler(Class<IO> ioClass, Class<D> dataClass, boolean debug, EnumMap<Options, Boolean> options, ClassDefiner definer) {
		this.ioClass = ioClass;
		this.dataClass = dataClass;
		this.debug = debug;
		this.options = options;
		this.definer = definer;
		this.self = "HyphenSerializer";
		this.methodDedup = this.options.get(Options.SHORT_METHOD_NAMES) ? new HashMap<>() : null;
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

		this.cw.visit(V16, ACC_PUBLIC | ACC_FINAL, this.self, null, GenUtil.internal(Object.class), new String[]{GenUtil.internal(HyphenSerializer.class)});

		try (var mh = new MethodHandler(this.cw.visitMethod(ACC_PUBLIC, "<init>", GenUtil.methodDesc(Void.TYPE), null, null), this.self, dataClass, ioClass)) {
			mh.visitIntInsn(ALOAD, 0);
			mh.callInst(INVOKESPECIAL, Object.class, "<init>", Void.TYPE);
			mh.op(RETURN);
		}

		if (options.get(Options.FAST_ALLOC)) {
			try (var mh = new MethodHandler(this.cw.visitMethod(ACC_STATIC, "<clinit>", GenUtil.methodDesc(Void.TYPE), null, null), this.self, dataClass, ioClass)) {
				mh.visitTypeInsn(NEW, self);
				mh.op(DUP);
				mh.visitMethodInsn(INVOKESPECIAL, self, "<init>", GenUtil.methodDesc(Void.TYPE), false);
				mh.visitFieldInsn(PUTSTATIC, ClassDefiner.class, "SERIALIZER", HyphenSerializer.class);
				mh.op(RETURN);
			}
		}
	}

	public MethodInfo apply(MethodInfo info) {
		if (methodDedup != null)
			info.setName(GenUtil.hyphenShortMethodName(methodDedup.computeIfAbsent(info, info1 -> new AtomicInteger(0)).getAndIncrement()), this);
		info.setName(GenUtil.makeSafe(info.getName()), this);
		return info;
	}

	public void writeMethods(Collection<MethodDef> methods) {
		for (MethodDef method : methods) {
			writeMethod(method, false);
		}
	}

	public void writeMethod(MethodDef def, boolean raw) {
		writeMethodInternal(def.clazz, def.getInfo, raw, def::writeMethodGet);
		writeMethodInternal(def.clazz, def.putInfo, raw, mh -> def.writeMethodPut(mh, () -> mh.varOp(ILOAD, "data")));
		writeMethodInternal(def.clazz, def.measureInfo, raw, mh -> def.writeMethodMeasure(mh, () -> mh.varOp(ILOAD, "data")));
	}

	private void writeMethodInternal(Clazz clazz, MethodInfo methodInfo, boolean raw, Consumer<MethodHandler> writer) {
		final Class<?>[] parameters = methodInfo.parameters;
		try (var mh = new MethodHandler(cw, methodInfo, self, dataClass, ioClass, raw, options.get(Options.SHORT_VARIABLE_NAMES))) {
			for (Class<?> parameter : parameters)
				mh.addVar(getVarName(parameter) + (raw ? "raw" : ""), raw ? Object.class : parameter);
			mh.visitCode();
			if (raw) {
				for (Class<?> parameter : parameters) {
					final String varName = getVarName(parameter);
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

	private String getVarName(Class<?> c) {
		if (c == ioClass) return "io";
		return "data";
	}

	public void setupSpark(MethodDef spark) {
		spark.getInfo.setName("get", this);
		spark.putInfo.setName("put", this);
		spark.measureInfo.setName("measure", this);
		writeMethod(spark, true);
	}

	@SuppressWarnings("unchecked")
	public synchronized <O extends HyphenSerializer<IO, D>> HyphenSerializer<IO, D> export() {
		final byte[] bytes = cw.toByteArray();

		if (debug) {
			try {
				Files.write(Path.of("./" + self + ".class"), bytes, StandardOpenOption.CREATE);
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
	}
}
