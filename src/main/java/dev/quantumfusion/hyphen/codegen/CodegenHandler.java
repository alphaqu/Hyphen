package dev.quantumfusion.hyphen.codegen;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.objectweb.asm.Opcodes.*;

public class CodegenHandler {
	private final IOHandler io;
	private final ClassWriter cw;

	public CodegenHandler(IOHandler io, String name) {
		this.io = io;
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		this.cw.visit(V16, ACC_PUBLIC + ACC_FINAL, name, null, Type.getInternalName(Object.class), null);
	}

	private void createConstructor() {
		try (MethodHandler mh = MethodHandler.createVoid(this.cw, this.io, ACC_PUBLIC, "<init>")) {
			mh.visitIntInsn(ALOAD, 0);
			mh.callSpecialMethod(Object.class, "<init>", Void.TYPE);
			mh.visitInsn(RETURN);
		}
	}

	private void createEncode() {
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

	private void createDecode() {
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

	public static void main(String[] args) throws IOException {
		CodegenHandler uwu = new CodegenHandler(IOHandler.ARRAY, "UwU");

		uwu.createConstructor();
		uwu.createEncode();
		uwu.createDecode();

		byte[] bytes = uwu.cw.toByteArray();

		Files.write(Path.of("./uwu.class"), bytes);
	}
}
