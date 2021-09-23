package dev.quantumfusion.hyphen.gen;

import dev.quantumfusion.hyphen.gen.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Map;
import java.util.function.Function;

import static org.objectweb.asm.Opcodes.*;

public class SerializerClassFactory {
	private final Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> definitions;
	private final IOMode mode;
	private final ClassWriter classWriter;

	public SerializerClassFactory(Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> definitions, IOMode mode) {
		this.definitions = definitions;
		this.mode = mode;
		this.classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		this.classWriter.visit(V16, ACC_PUBLIC, "Serializer", null, Type.getInternalName(Object.class), null);
		createConstructor();
	}

	private void createConstructor() {
		MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", GenUtil.getVoidMethodDesc(), null, null);
		mv.visitIntInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	public void createMethod(TypeInfo typeInfo, SerializerMetadata serializerMetadata) {
		writeEncode(typeInfo, serializerMetadata);
		writeDecode(typeInfo, serializerMetadata);
	}

	private void writeEncode(TypeInfo typeInfo, SerializerMetadata serializerMetadata) {
		MethodVisitor mv = createMethodName(typeInfo, "_encode", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(typeInfo.clazz), Type.getType(mode.ioClass)));
		VarHandler varHandler = new VarHandler(mv);
		varHandler.createVar("data", typeInfo.getClazz());
		varHandler.createVar("io", mode.ioClass);
		Label start = new Label();
		mv.visitLabel(start);

		serializerMetadata.writeEncode(mv, typeInfo, new Context(mode, varHandler, Type.getType("L" + "Serializer ")));
		Label stop = new Label();
		mv.visitLabel(stop);
		varHandler.applyLocals(mv, start, stop);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private void writeDecode(TypeInfo typeInfo, SerializerMetadata serializerMetadata) {
		MethodVisitor mv = createMethodName(typeInfo, "_decode", Type.getMethodDescriptor(Type.getType(typeInfo.clazz), Type.getType(mode.ioClass)));
		VarHandler varHandler = new VarHandler(mv);
		varHandler.createVar("io", mode.ioClass);
		Label start = new Label();
		mv.visitLabel(start);

		serializerMetadata.writeDecode(mv, typeInfo, new Context(mode, varHandler, Type.getType("L" + "Serializer ")));

		mv.visitInsn(ARETURN);
		Label stop = new Label();
		mv.visitLabel(stop);
		varHandler.applyLocals(mv, start, stop);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private MethodVisitor createMethodName(TypeInfo info, String suffix, String methodDescriptor) {
		return classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, info.getMethodName(false) + suffix, methodDescriptor, null, null);
	}

	public Class<?> compile() {
		// the finished serializer
		byte[] b = compileCode();
		return new Loader().addClass("serializer", b, 0, b.length);
	}

	public byte[] compileCode() {
		return classWriter.toByteArray();
	}

	private static class Loader extends ClassLoader {
		public Loader() {
			super(Thread.currentThread().getContextClassLoader());
		}

		public Class<?> addClass(String name, byte[] b, int off, int len) {
			return defineClass(name, b, off, len);
		}
	}
}
