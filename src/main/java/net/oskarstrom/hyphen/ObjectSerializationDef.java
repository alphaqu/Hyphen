package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.util.Color;

public interface ObjectSerializationDef {
	Class<?> getType();

	default String toFancyString(){
		return Color.RED + this.toString();
	}
}
