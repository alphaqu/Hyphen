package dev.quantumfusion.hyphen.codegen;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

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
		try (MethodHandler mh = MethodHandler.createVoid(cw, io, ACC_PUBLIC, "<init>")) {
			mh.visitIntInsn(ALOAD, 0);
			mh.callSpecialMethod(Object.class, "<init>", Void.TYPE);
			mh.visitInsn(RETURN);
		}
	}

	private void createEncode() {
		try (MethodHandler mh = MethodHandler.createVoid(cw, io, ACC_PUBLIC, "encodeshit")) {
			mh.callIOPut(int.class);
		}
	}
}
