package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.gen.Context;
import org.objectweb.asm.MethodVisitor;

public abstract class SerializerDef {

	public abstract void writePut(MethodVisitor mv, Context ctx);

	public abstract void writeGet(MethodVisitor mv, Context ctx);
}
