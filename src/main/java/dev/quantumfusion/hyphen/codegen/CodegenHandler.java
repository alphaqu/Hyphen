package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.HyphenException;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class CodegenHandler {
	final String name;
	final ClassWriter cw;
	final EnumMap<Options, Boolean> options;
	private final EnumMap<MethodType, Map<TypeInfo, MethodInfo>> methods;
	private final IOMode io;

	public CodegenHandler(EnumMap<Options, Boolean> options, Map<TypeInfo, MethodMetadata<?>> methodWriters, Class<?> ioClazz, String name) {
		this.options = options;
		this.io = IOMode.create(ioClazz);
		this.name = name;
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		this.cw.visit(V16, ACC_PUBLIC + ACC_FINAL, name, null, Type.getInternalName(Object.class), new String[]{Type.getInternalName(HyphenSerializer.class)});
		this.methods = parseMethodNames(methodWriters);
	}

	private EnumMap<MethodType, Map<TypeInfo, MethodInfo>> parseMethodNames(Map<TypeInfo, MethodMetadata<?>> methodWriters) {
		var out = new EnumMap<MethodType, Map<TypeInfo, MethodInfo>>(MethodType.class);
		for (var type : MethodType.values()) {
			final var values = new HashMap<TypeInfo, MethodInfo>();
			for (var entry : methodWriters.entrySet()) {
				values.put(entry.getKey(), MethodInfo.create(this, entry.getValue(), type));
			}
			out.put(type, values);
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

	public void createMainMethods(MethodMetadata<?> writer) {
		for (MethodType value : MethodType.values()) {
			createMethod(MethodInfo.create(this, writer, value), true);
		}
	}

	public void createMethods() {
		methods.values().forEach((methods) -> {
			for (MethodInfo value : methods.values())
				createMethod(value, false);
		});
	}

	public MethodInfo getMethodData(MethodType type, TypeInfo typeInfo) {
		return methods.get(type).get(typeInfo);
	}

	public void createMethod(MethodInfo methodInfo, boolean main) {
		var type = methodInfo.type();
		var info = methodInfo.info();
		var writer = methodInfo.writer();

		if (options.get(type.disableOption) && !main) return;


		var methodName = main ? type.name().toLowerCase() : methodInfo.name;
		var methodReturn = main ? (methodInfo.returnClass == info.getClazz() ? Object.class : methodInfo.returnClass) : methodInfo.returnClass;
		Class[] methodParam;
		if (main) {
			final Class<?>[] methodParam1 = new Class[methodInfo.parameters.length];
			Arrays.fill(methodParam1, Object.class);
			methodParam = methodParam1;
		} else methodParam = methodInfo.parameters;
		try (MethodHandler mh = MethodHandler.create(this, (main ? 0 : ACC_STATIC) + ACC_PUBLIC | ACC_FINAL, methodName, methodReturn, methodParam)) {
			if (!options.get(type.disableOption)) {
				if (main) {
					mh.createVar("this", Object.class);
					for (Vars parameter : methodInfo.type.parameters)
						mh.createVar(parameter.name().toLowerCase() + "Raw", Object.class);
				}

				for (Vars parameter : methodInfo.type.parameters)
					parameter.createVar(mh, this, info);

				//cast
				if (main) {
					for (Vars parameter : methodInfo.type.parameters) {
						mh.getVar(parameter.name().toLowerCase() + "Raw").load();
						mh.cast(parameter.classGetter.apply(this, info));
						mh.getVar(parameter.name().toLowerCase()).store();
					}
				}

				type.writer.accept(writer, mh);
			} else {
				ThrowHandler.fatalGen(mh, HyphenException.class, type.name().toLowerCase() + "() is disabled");
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

	public record MethodInfo(TypeInfo info, MethodMetadata<?> writer, String name, Class<?> returnClass, Class<?>[] parameters, MethodType type) {
		public static MethodInfo create(CodegenHandler ch, MethodMetadata<?> writer, MethodType type) {
			final TypeInfo info = writer.getInfo();
			return new MethodInfo(info, writer, type.parseMethodName(info), type.getReturn(ch, info), type.getParameters(ch, info), type);
		}
	}
}
