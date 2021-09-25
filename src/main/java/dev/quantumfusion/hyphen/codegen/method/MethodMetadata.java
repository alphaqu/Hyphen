package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.codegen.IOHandler;
import dev.quantumfusion.hyphen.gen.VarHandler;
import dev.quantumfusion.hyphen.info.TypeInfo;
import org.objectweb.asm.MethodVisitor;

public abstract class MethodMetadata {
	protected final TypeInfo info;

	public MethodMetadata(TypeInfo info) {
		this.info = info;
	}

	public abstract void writePut(MethodVisitor mv, IOHandler io, VarHandler var);

	public abstract void writeGet(MethodVisitor mv, IOHandler io, VarHandler var);
}
