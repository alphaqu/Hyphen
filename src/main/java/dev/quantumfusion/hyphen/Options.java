package dev.quantumfusion.hyphen;

public enum Options {
	COMPACT_VARIABLES(true),
	COMPACT_METHODS(true),
	DISABLE_MEASURE(false),
	DISABLE_PUT(false),
	DISABLE_GET(false);

	public boolean defaultValue;

	Options(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}
}
