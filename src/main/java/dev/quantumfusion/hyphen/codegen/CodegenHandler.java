package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.objectweb.asm.Opcodes.*;

public class CodegenHandler {
	private final IOHandler io;
	private final String name;
	private final ClassWriter cw;

	public CodegenHandler(IOHandler io, String name) {
		this.io = io;
		this.name = name;
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		this.cw.visit(V16, ACC_PUBLIC + ACC_FINAL, name, null, Type.getInternalName(Object.class), null);
	}

	public static void main(String[] args) throws IOException {
		CodegenHandler uwu = new CodegenHandler(IOHandler.ARRAY, "UwU");

		uwu.createConstructor();

		byte[] bytes = uwu.cw.toByteArray();


		Files.write(Path.of("./uwu.class"), bytes);
	}

	public void createConstructor() {
		try (MethodHandler mh = MethodHandler.createVoid(this.cw, this.io, ACC_PUBLIC, "<init>")) {
			mh.visitIntInsn(ALOAD, 0);
			mh.callSpecialMethod(Object.class, "<init>", Void.TYPE);
			mh.visitInsn(RETURN);
		}
	}

	public void createEncode(TypeInfo info, MethodMetadata methodMetadata) {
		try (MethodHandler mh = MethodHandler.createVoid(this.cw, this.io, ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "encodeshit", this.io.ioClass, Integer.class)) {
			var io = mh.createVar("io", this.io.ioClass);
			var data = mh.createVar("data", Integer.class);

			io.load();
			data.load();
			// io Integer
			mh.callInstanceMethod(Integer.class, "intValue", int.class);
			// io int
			mh.callIOPut(int.class);
			mh.visitInsn(RETURN);
		}
	}

	public void createDecode(TypeInfo info, MethodMetadata methodMetadata) {
		try (MethodHandler mh = MethodHandler.create(this.cw, this.io, ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "decodeshit", Integer.class, this.io.ioClass)) {
			var io = mh.createVar("io", this.io.ioClass);

			io.load();
			// io
			mh.callIOGet(int.class);
			// int
			mh.callStaticMethod(Integer.class, "valueOf", Integer.class, int.class);
			// Integer
			mh.visitInsn(ARETURN);
		}
	}

	public Class<?> export() {
		return new ClassLoader() {
			public Class<?> define(byte[] bytes, String name) {
				return super.defineClass(name, bytes, 0, bytes.length);
			}
		}.define(cw.toByteArray(), name);
	}
}
