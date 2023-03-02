package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.SerializerGenerator;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.scan.type.ParaClazz;

import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class ListDef extends IndexedDef {
	public ListDef(Clazz clazz) {
		super("list", clazz);
	}

	@Override
	public Clazz scanComponent(SerializerGenerator<?, ?> handler) {
		ParaClazz paraClazz = (ParaClazz) clazz;
		return paraClazz.define("E");
	}

	@Override
	public void writeGetElement(MethodHandler mh) {
		mh.callInst(INVOKEINTERFACE, List.class, "get", Object.class, int.class);
		mh.typeOp(CHECKCAST, component.getBytecodeClass());
	}

	@Override
	public void writeLength(MethodHandler mh) {
		mh.callInst(INVOKEINTERFACE, List.class, "size", int.class);
	}

	@Override
	public void writeGetConverter(MethodHandler mh) {
		mh.callInst(INVOKESTATIC, Arrays.class, "asList", List.class, Object[].class);
	}
}