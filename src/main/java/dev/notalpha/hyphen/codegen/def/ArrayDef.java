package dev.notalpha.hyphen.codegen.def;

import dev.notalpha.hyphen.SerializerGenerator;
import dev.notalpha.hyphen.codegen.MethodWriter;
import dev.notalpha.hyphen.scan.struct.ArrayStruct;
import dev.notalpha.hyphen.scan.struct.Struct;

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
