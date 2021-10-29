package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.type.ArrayClazz;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.ARRAYLENGTH;

public class ArrayDef extends IndexedDef {
	protected final SerializerDef componentDef;
	protected final Clazz component;

	public ArrayDef(SerializerHandler<?, ?> handler, Clazz clazz, Clazz component) {
		super("arr", handler, clazz, component, (mh) -> mh.op(AALOAD), (mh) -> mh.op(ARRAYLENGTH));
		this.component = component;
		this.componentDef = handler.acquireDef(component);
	}

	public ArrayDef(SerializerHandler<?, ?> handler, ArrayClazz clazz) {
		this(handler, clazz, clazz.component);
	}

	@Override
	public void writeGetConverter(MethodHandler mh) {}
}
