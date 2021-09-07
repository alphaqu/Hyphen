package net.oskarstrom.hyphen.gen.impl;

import net.oskarstrom.hyphen.ObjectSerializationDef;
import net.oskarstrom.hyphen.data.ClassInfo;
import net.oskarstrom.hyphen.util.Color;

public class MethodCallDef extends AbstractDef {
	public final ClassInfo info;

	public MethodCallDef(ClassInfo info) {
		this.info = info;
	}

	@Override
	public Class<?> getType() {
		return Object.class;
	}


}
