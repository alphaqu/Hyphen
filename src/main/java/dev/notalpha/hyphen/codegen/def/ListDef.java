package dev.notalpha.hyphen.codegen.def;

import dev.notalpha.hyphen.SerializerGenerator;
import dev.notalpha.hyphen.codegen.MethodWriter;
import dev.notalpha.hyphen.scan.struct.ClassStruct;
import dev.notalpha.hyphen.scan.struct.Struct;

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