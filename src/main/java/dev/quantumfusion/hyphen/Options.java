package dev.quantumfusion.hyphen;

public enum Options {
	COMPACT_VARIABLES(true),
	COMPACT_METHODS(true),
	DISABLE_MEASURE(false),
	DISABLE_ENCODE(false),
	DISABLE_DECODE(false);

	public boolean defaultValue;

	Options(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}
}
