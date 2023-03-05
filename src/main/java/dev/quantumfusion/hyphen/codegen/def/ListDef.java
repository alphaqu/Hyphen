package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerGenerator;
import dev.quantumfusion.hyphen.codegen.MethodWriter;
import dev.quantumfusion.hyphen.scan.struct.ClassStruct;
import dev.quantumfusion.hyphen.scan.struct.Struct;

import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class ListDef extends IndexedDef<ClassStruct> {
	public ListDef(Struct clazz) {
		super("list", (ClassStruct) clazz);
	}

	@Override
	public Struct scanComponent(SerializerGenerator<?, ?> handler) {
		return struct.getParameter("E");
	}

	@Override
	public void writeGetElement(MethodWriter mh) {
		mh.callInst(INVOKEINTERFACE, List.class, "get", Object.class, int.class);
		mh.typeOp(CHECKCAST, component.getBytecodeClass());
	}

	@Override
	public void writeLength(MethodWriter mh) {
		mh.callInst(INVOKEINTERFACE, List.class, "size", int.class);
	}

	@Override
	public void writeGetConverter(MethodWriter mh) {
		mh.callInst(INVOKESTATIC, Arrays.class, "asList", List.class, Object[].class);
	}
}