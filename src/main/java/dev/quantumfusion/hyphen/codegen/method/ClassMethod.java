package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.info.ClassInfo;
import org.objectweb.asm.MethodVisitor;

public class ClassMethod extends MethodMetadata {

	public ClassMethod(ClassInfo info) {
		super(info);
	}

	@Override
	public void writePut(MethodVisitor mv, Context ctx) {

	}

	@Override
	public void writeGet(MethodVisitor mv, Context ctx) {

	}
}
