package net.oskarstrom.hyphen.gen.impl;

import net.oskarstrom.hyphen.ObjectSerializationDef;
import net.oskarstrom.hyphen.data.FieldInfo;
import net.oskarstrom.hyphen.util.Color;

public abstract class AbstractDef implements ObjectSerializationDef {

	public String getString(FieldInfo fieldInfo, boolean fancyPrint) {
		return (fancyPrint ? Color.RED : "") + "|||> " + (fancyPrint ? Color.CYAN : "") + fieldInfo.parseMethodName() + (fancyPrint ? Color.WHITE : "") + "()";
	}
}
