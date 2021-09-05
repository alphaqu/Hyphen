package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.data.FieldInfo;

public interface ObjectSerializationDef {
	Class<?> getType();

	String getString(FieldInfo fieldInfo, boolean fancyPrint);
}
