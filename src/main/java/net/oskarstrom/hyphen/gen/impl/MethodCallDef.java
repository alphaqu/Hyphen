package net.oskarstrom.hyphen.gen.impl;

import net.oskarstrom.hyphen.ObjectSerializationDef;
import net.oskarstrom.hyphen.data.ClassInfo;
import net.oskarstrom.hyphen.data.TypeInfo;
import net.oskarstrom.hyphen.util.Color;

public class MethodCallDef extends AbstractDef {
	public final TypeInfo info;

	public MethodCallDef(TypeInfo info) {
		this.info = info;
	}

	@Override
	public Class<?> getType() {
		return Object.class;
	}

	@Override
	public String toString() {
		return "MethodCallDef{" + this.info.toString() + '}';
	}

	@Override
	public String toFancyString() {
		return Color.GREEN + "MethodCallDef{" + this.info.toFancyString() + Color.GREEN + '}';
	}
}
