package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.codegen.def.MethodDef;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
	private final EnumMap<Options, Boolean> options;

	// Method Dedup
	@Nullable
	private final Map<MethodInfo, AtomicInteger> methodDedup;

	// cw
	private final ClassWriter cw;

	public CodegenHandler(Class<IO> ioClass, Class<D> dataClass, boolean debug, EnumMap<Options, Boolean> options) {
		this.ioClass = ioClass;
		this.dataClass = dataClass;
		this.debug = debug;
		this.options = options;
		this.self = "UWU";
		this.methodDedup = this.options.get(Options.SHORT_METHOD_NAMES) ? new HashMap<>() : null;

		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		this.cw.visit(V16, ACC_PUBLIC | ACC_FINAL, this.self, null, GenUtil.internal(Object.class), new String[]{GenUtil.internal(HyphenSerializer.class)});
		try (var mh = new MethodHandler(this.cw.visitMethod(ACC_PUBLIC, "<init>", GenUtil.methodDesc(Void.TYPE), null, null), this.self, dataClass, ioClass)) {
			mh.visitIntInsn(ALOAD, 0);
			mh.visitMethodInsn(INVOKESPECIAL, Object.class, "<init>", Void.TYPE);
			mh.op(RETURN);
		}
	}

	public MethodInfo apply(MethodInfo info) {
		if (methodDedup != null)
			info.setName(GenUtil.hyphenShortMethodName(methodDedup.computeIfAbsent(info, info1 -> new AtomicInteger(0)).getAndIncrement()), this);
		return info;
	}

	public void writeMethod(MethodDef def, boolean raw) {
		writeMethodInternal(def.getInfo, raw, def::writeMethodGet);
		writeMethodInternal(def.putInfo, raw, def::writeMethodPut);
		writeMethodInternal(def.measureInfo, raw, def::writeMethodMeasure);
	}

	private void writeMethodInternal(MethodInfo methodInfo, boolean raw, Consumer<MethodHandler> writer) {
		final Class<?>[] parameters = methodInfo.parameters;
		try (var mh = new MethodHandler(cw, methodInfo, self, dataClass, ioClass, raw, options.get(Options.SHORT_VARIABLE_NAMES))) {
			for (Class<?> parameter : parameters)
				mh.addVar(getVarName(parameter) + (raw ? "raw" : ""), raw ? Object.class : parameter);
			if (raw) {
				for (Class<?> parameter : parameters) {
					final String varName = getVarName(parameter);
					mh.addVar(varName, parameter); // add var
					// cast
					mh.varOp(ILOAD, varName + "raw");
					mh.visitTypeInsn(CHECKCAST, parameter);
					mh.varOp(ISTORE, varName);
				}
			}

			writer.accept(mh);
		}
	}

	private String getVarName(Class<?> c) {
		if(c == ioClass) return "io";
		return "data";
	}

	public void setupSpark(MethodDef spark) {
		spark.getInfo.setName("get",this);
		spark.putInfo.setName("put",this);
		spark.measureInfo.setName("measure",this);
		writeMethod(spark, true);
	}

	public HyphenSerializer<IO, D> export() {
		final byte[] bytes = cw.toByteArray();
		final Class<?> hyphen = new ClassLoader(Thread.currentThread().getContextClassLoader()) {
			public Class<?> def() {
				return defineClass(self, bytes, 0, bytes.length);
			}
		}.def();

		try {
			Files.write(Path.of("./UWU.class"), bytes, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			return (HyphenSerializer<IO, D>) hyphen.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
}
