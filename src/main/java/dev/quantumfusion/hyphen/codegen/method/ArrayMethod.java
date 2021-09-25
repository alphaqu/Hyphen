package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.codegen.IOHandler;
import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.gen.VarHandler;
import dev.quantumfusion.hyphen.info.ArrayInfo;
import org.objectweb.asm.MethodVisitor;

public class ArrayMethod extends MethodMetadata {
	private final SerializerDef component;

	public ArrayMethod(ArrayInfo info, SerializerDef component) {
		super(info);
		this.component = component;
	}

	@Override
	public void writePut(MethodVisitor mv, IOHandler io, VarHandler var) {

	}

	@Override
	public void writeGet(MethodVisitor mv, IOHandler io, VarHandler var) {

	}
}
