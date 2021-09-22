package dev.quantumfusion.hyphen.gen.impl;

import dev.quantumfusion.hyphen.ObjectSerializationDef;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.Color;

public class ArrayDef implements ObjectSerializationDef {
	private final TypeInfo component;

	public ArrayDef(TypeInfo component) {
		this.component = component;
	}

	@Override
	public Class<?> getType() {
		return component.clazz;
	}

	@Override
	public String toFancyString() {
		return component.toFancyString() + Color.PURPLE + "[]";
	}
}
