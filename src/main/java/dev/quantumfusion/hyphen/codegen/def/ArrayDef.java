package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerGenerator;
import dev.quantumfusion.hyphen.codegen.MethodWriter;
import dev.quantumfusion.hyphen.scan.struct.ArrayStruct;
import dev.quantumfusion.hyphen.scan.struct.Struct;

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.ARRAYLENGTH;

public class ArrayDef extends IndexedDef<ArrayStruct> {

	public ArrayDef(ArrayStruct clazz) {
		super("arr",  clazz);
	}

	@Override
	public Struct scanComponent(SerializerGenerator<?, ?> handler) {
		return struct.component;
	}

	@Override
	public void writeGetElement(MethodWriter mh) {
		mh.op(AALOAD);
	}

	@Override
	public void writeLength(MethodWriter mh) {
		mh.op(ARRAYLENGTH);
	}

	@Override
	public void writeGetConverter(MethodWriter mh) {
	}
}
