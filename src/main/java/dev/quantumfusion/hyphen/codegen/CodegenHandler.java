package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;

public class CodegenHandler {
	final String name;
	final ClassWriter cw;
	final EnumMap<Options, Boolean> options;
	private final Map<TypeInfo, MethodMetadata> methodWriters;
	private final EnumMap<MethodMode, Map<TypeInfo, MethodInfo>> methods;
	private final IOMode io;

	public CodegenHandler(EnumMap<Options, Boolean> options, Map<TypeInfo, MethodMetadata> methodWriters, Class<?> ioClazz, String name) {
		this.options = options;
		this.methodWriters = methodWriters;
		this.io = IOMode.create(ioClazz);
		this.name = name;
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		this.cw.visit(V16, ACC_PUBLIC + ACC_FINAL, name, null, Type.getInternalName(Object.class), new String[]{Type.getInternalName(HyphenSerializer.class)});
		this.methods = parseMethodNames(methodWriters);
	}

	private EnumMap<MethodMode, Map<TypeInfo, MethodInfo>> parseMethodNames(Map<TypeInfo, MethodMetadata> methods) {
		var out = new EnumMap<MethodMode, Map<TypeInfo, MethodInfo>>(MethodMode.class);

		Map<List<Class<?>>, AtomicInteger> dedup = null;
		if (options.get(Options.COMPACT_METHODS))
			dedup = new HashMap<>();

		for (MethodMode mode : MethodMode.values()) {
			final HashMap<TypeInfo, MethodInfo> methodNames = new HashMap<>();
			for (MethodMetadata methodMetadata : methods.values()) {
				final TypeInfo info = methodMetadata.getInfo();
				final String name;
				final Class<?> returnClass = mode.returnClass == null ? info.getClazz() : mode.returnClass;
				final Class<?>[] param = mode.paramFunc.apply(info, this);

				if (options.get(Options.COMPACT_METHODS)) {
					List<Class<?>> paramList = List.of(param);
					if (dedup.containsKey(paramList)) dedup.get(paramList).incrementAndGet();
					else dedup.put(paramList, new AtomicInteger());

					name = String.valueOf(dedup.get(paramList).get());
				} else {
					name = mode.prefix + info.getMethodName(false);
				}

				methodNames.put(info, new MethodInfo(info, name, returnClass, param));
			}
			out.put(mode, methodNames);
		}
		return out;
	}

	public void createConstructor() {
		try (MethodHandler mh = MethodHandler.createVoid(this, ACC_PUBLIC, "<init>")) {
			mh.visitIntInsn(ALOAD, 0);
			mh.callSpecialMethod(Object.class, "<init>", Void.TYPE);
			mh.returnOp();
		}

	}

	public MethodInfo getMethodData(MethodMode mode, TypeInfo typeInfo) {
		return methods.get(mode).get(typeInfo);
	}

	public MethodHandler createHyphenMethod(MethodMode mode, MethodMetadata methodMetadata) {
		final int tag = ACC_STATIC | ACC_PUBLIC | ACC_FINAL;
		final MethodInfo methodData = getMethodData(mode, methodMetadata.getInfo());
		return MethodHandler.create(this, tag, methodData.name(), methodData.returnClass(), methodData.param());
	}

	public void createMethods() {
		if (!options.get(Options.DISABLE_ENCODE)) {
			methodWriters.values().forEach(methodMetadata -> methodMetadata.createPut(this));
		}
		if (!options.get(Options.DISABLE_DECODE)) {
			methodWriters.values().forEach(methodMetadata -> methodMetadata.createGet(this));
		}
		if (!options.get(Options.DISABLE_MEASURE)) {
			methodWriters.values().forEach(methodMetadata -> methodMetadata.createMeasure(this));
		}
	}

	public void createMainMethods(MethodMetadata mainSerializeMethod) {
		this.createMainEncode(mainSerializeMethod);
		this.createMainDecode(mainSerializeMethod);
		this.createMainMeasure(mainSerializeMethod);
	}

	private void createMainEncode(MethodMetadata methodMetadata) {
		try (MethodHandler mh = MethodHandler.createVoid(
				this,
				ACC_PUBLIC | ACC_FINAL,
				"encode",
				Object.class,
				Object.class)
		) {
			if (!options.get(Options.DISABLE_ENCODE)) {
				mh.createVar("this", Object.class);
				var ioRaw = mh.createVar("ioRaw", Object.class);
				var dataRaw = mh.createVar("dataRaw", Object.class);
				var io = mh.createVar("io", this.io.ioClass);
				var data = mh.createVar("data", methodMetadata.getInfo().getClazz());

				ioRaw.load();
				mh.cast(this.io.ioClass);
				io.store();

				dataRaw.load();
				mh.cast(methodMetadata.getInfo().getClazz());
				data.store();
				// TODO should this call the static put instead?
				//  methodMetadata.callPut(mh);
				methodMetadata.writePut(mh, io, data);
			} else {
				//TODO throw exception
				mh.visitInsn(ICONST_0);
				mh.visitInsn(I2L);
				mh.returnOp();
			}
		}
	}

	private void createMainDecode(MethodMetadata methodMetadata) {
		try (MethodHandler mh = MethodHandler.create(
				this,
				ACC_PUBLIC | ACC_FINAL,
				"decode",
				Object.class,
				Object.class)) {
			if (!options.get(Options.DISABLE_DECODE)) {
				mh.createVar("this", Object.class);
				var ioRaw = mh.createVar("ioRaw", Object.class);
				var io = mh.createVar("io", this.io.ioClass);
				ioRaw.load();
				mh.cast(this.io.ioClass);
				io.store();

				// TODO should this call the static get instead?
				//  methodMetadata.callPut(mh);
				methodMetadata.writeGet(mh, io);
			} else {
				//TODO throw exception
				mh.visitInsn(ICONST_0);
				mh.visitInsn(I2L);
				mh.returnOp();
			}
		}
	}

	private void createMainMeasure(MethodMetadata methodMetadata) {
		TypeInfo info = methodMetadata.getInfo();
		try (MethodHandler mh = MethodHandler.create(
				this,
				ACC_PUBLIC | ACC_FINAL,
				"measure",
				long.class,
				Object.class)
		) {
			if (!options.get(Options.DISABLE_MEASURE)) {
				long size = methodMetadata.getSize();
				if (!methodMetadata.dynamicSize()) {
					mh.visitLdcInsn(size);
				} else {
					mh.createVar("this", Object.class);
					var dataRaw = mh.createVar("dataRaw", Object.class);
					var data = mh.createVar("data", info.getClazz());
					dataRaw.load();
					mh.cast(info.getClazz());
					data.store();
					methodMetadata.writeMeasure(mh, data);
				}
			} else {
				//TODO throw exception
				mh.visitInsn(ICONST_0);
				mh.visitInsn(I2L);
				mh.returnOp();
			}
		}
	}

	public byte[] byteArray() {
		return this.cw.toByteArray();
	}

	public Class<?> export() {
		return new ClassLoader() {
			Class<?> define(byte[] bytes, String name) {
				return super.defineClass(name, bytes, 0, bytes.length);
			}
		}.define(this.cw.toByteArray(), this.name);
	}

	public IOMode getIOMode() {
		return this.io;
	}

	public record MethodInfo(TypeInfo info, String name, Class<?> returnClass, Class<?>... param) {
	}

}
