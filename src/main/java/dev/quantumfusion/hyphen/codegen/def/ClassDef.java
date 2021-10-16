package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import java.util.HashMap;
import java.util.Map;

public class ClassDef extends MethodDef {
	protected final Map<FieldEntry, SerializerDef> fields = new HashMap<>();

	public ClassDef(SerializerHandler<?, ?> handler, Clazz clazz) {
		super(handler.codegenHandler, clazz.toString());
		for (FieldEntry field : clazz.getFields()) {
			fields.put(field, handler.acquireDef(field.clazz()));
		}
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
