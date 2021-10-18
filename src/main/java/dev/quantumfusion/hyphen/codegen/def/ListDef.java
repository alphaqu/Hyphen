package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.scan.type.ParaClazz;

import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class ListDef extends IndexedDef {

	public ListDef(SerializerHandler<?, ?> handler, Clazz clazz, Clazz component) {
		super(handler, clazz, component, (mh) -> {
			mh.callInst(INVOKEINTERFACE, List.class, "get", Object.class, int.class);
			mh.typeOp(CHECKCAST, component.getBytecodeClass());
		}, (mh) -> {
			mh.callInst(INVOKEINTERFACE, List.class, "size", int.class);
		});
	}

	public ListDef(SerializerHandler<?, ?> handler, ParaClazz clazz) {
		this(handler, clazz, clazz.define("E"));
	}

	@Override
	public void writeGetConverter(MethodHandler mh) {
		mh.callInst(INVOKESTATIC, Arrays.class, "asList", List.class, Object[].class);
	}
}