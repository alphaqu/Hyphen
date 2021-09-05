package net.oskarstrom.hyphen.gen.impl;

import net.oskarstrom.hyphen.ObjectSerializationDef;
import net.oskarstrom.hyphen.data.FieldInfo;
import net.oskarstrom.hyphen.util.Color;

public class IntDef extends AbstractDef {
	@Override
	public Class<?> getType() {
		return int.class;
	}

	public String getString(FieldInfo fieldInfo, boolean fancyPrint) {
		return (fancyPrint ? Color.RED : "") + fieldInfo.parseMethodName();
	}
}
