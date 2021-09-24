package dev.quantumfusion.hyphen.gen;

import dev.quantumfusion.hyphen.gen.impl.ObjectSerializationDef;
import dev.quantumfusion.hyphen.gen.metadata.ClassSerializerMetadata;
import dev.quantumfusion.hyphen.gen.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
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
		MethodVisitor mv;
		if (ClassSerializerMetadata.MODE == 0) {
			mv = createMethodName(typeInfo, "_encode", GenUtil.getVoidMethodDesc(typeInfo.clazz, mode.ioClass));
		} else {
			mv = createMethodName(typeInfo, "_encode", GenUtil.getVoidMethodDesc(mode.ioClass, typeInfo.clazz));
		}

		VarHandler varHandler = new VarHandler(mv);
		varHandler.pushScope();
		VarHandler.Var io;
		VarHandler.Var data;
		if (ClassSerializerMetadata.MODE == 1) {
			io = varHandler.createVar("io", mode.ioClass);
			data = varHandler.createVar("data", typeInfo.getClazz());
		} else {
			data = varHandler.createVar("data", typeInfo.getClazz());
			io = varHandler.createVar("io", mode.ioClass);
		}
		serializerMetadata.writeEncode(mv, typeInfo, new Context(mode, varHandler, Type.getType("L" + "Serializer "), () -> mv.visitIntInsn(ALOAD, data.index()), () -> mv.visitIntInsn(ALOAD, io.index())));
		varHandler.popScope();
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private void writeDecode(TypeInfo typeInfo, SerializerMetadata serializerMetadata) {
		MethodVisitor mv = createMethodName(typeInfo, "_decode", Type.getMethodDescriptor(Type.getType(typeInfo.clazz), Type.getType(mode.ioClass)));
		VarHandler varHandler = new VarHandler(mv);
		varHandler.pushScope();

		var io = varHandler.createVar("io", mode.ioClass);

		serializerMetadata.writeDecode(mv, typeInfo, new Context(mode, varHandler, Type.getType("L" + "Serializer "), null, () -> mv.visitIntInsn(ALOAD, io.index())));

		mv.visitInsn(ARETURN);
		varHandler.popScope();

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private MethodVisitor createMethodName(TypeInfo info, String suffix, String methodDescriptor) {
		return this.classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, info.getMethodName(false) + suffix, methodDescriptor, null, null);
	}

	public Class<?> compile() {
		// the finished serializer
		byte[] b = this.compileCode();
		try {
			// FIXME
			Files.write(Path.of("./Serializer.class"), b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		CheckClassAdapter.verify(new ClassReader(b), null, true, new PrintWriter(System.out));
		return new Loader().addClass("Serializer", b, 0, b.length);
	}

	public byte[] compileCode() {
		return this.classWriter.toByteArray();
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
