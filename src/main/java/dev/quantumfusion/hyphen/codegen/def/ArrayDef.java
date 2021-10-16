package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.type.ArrayClazz;

public class ArrayDef extends MethodDef {
	protected final SerializerDef componentDef;

	public ArrayDef(SerializerHandler<?, ?> handler, ArrayClazz clazz) {
		super(handler.codegenHandler, clazz.toString());
		this.componentDef = handler.acquireDef(clazz.component);
	}

	@Override
	void writeMethodGet(MethodHandler mh) {

	}

	@Override
	void writeMethodPut(MethodHandler mh) {

	}

	@Override
	void writeMethodMeasure(MethodHandler mh) {

	}
}
