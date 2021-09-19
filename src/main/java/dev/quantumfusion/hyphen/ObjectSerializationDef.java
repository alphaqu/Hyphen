package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.util.Color;

public interface ObjectSerializationDef {
	Class<?> getType();

	default String toFancyString() {
		return Color.RED + this.toString();
	}
}
