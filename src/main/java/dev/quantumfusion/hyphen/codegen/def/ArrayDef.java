package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.type.ArrayClazz;

public class ArrayDef extends MethodDef {
	protected final SerializerDef componentDef;

	public ArrayDef(SerializerHandler<?, ?> handler, ArrayClazz clazz) {
		super(handler.codegenHandler, clazz);
		this.componentDef = handler.acquireDef(clazz.component);
	}

	@Override
	public void writeMethodGet(MethodHandler mh) {

	}

	@Override
	public void writeMethodPut(MethodHandler mh) {

	}

	@Override
	public void writeMethodMeasure(MethodHandler mh) {

	}
}
