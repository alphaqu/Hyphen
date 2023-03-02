package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.SerializerGenerator;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.type.ArrayClazz;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.ARRAYLENGTH;

public class ArrayDef extends IndexedDef {

	public ArrayDef(Clazz clazz) {
		super("arr",  clazz);
	}

	@Override
	public Clazz scanComponent(SerializerGenerator<?, ?> handler) {
		ArrayClazz arrayClazz = (ArrayClazz) clazz;
		return arrayClazz.component;
	}

	@Override
	public void writeGetElement(MethodHandler mh) {
		mh.op(AALOAD);
	}

	@Override
	public void writeLength(MethodHandler mh) {
		mh.op(ARRAYLENGTH);
	}

	@Override
	public void writeGetConverter(MethodHandler mh) {
	}
}
