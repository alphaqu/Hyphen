package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.IOInterface;

public enum Options {


	/**
	 * Disables the {@link HyphenSerializer#put(IOInterface, Object)} (Object, Object)} Method. Usage will result in a {@link UnsupportedOperationException}
	 */
	DISABLE_PUT(false),
	/**
	 * Disables the {@link HyphenSerializer#get(IOInterface)} Method. Usage will result in a {@link UnsupportedOperationException}
	 */
	DISABLE_GET(false),
	/**
	 * Disables the {@link HyphenSerializer#measure(Object)} Method. Usage will result in a {@link UnsupportedOperationException}
	 */
	DISABLE_MEASURE(false),

	/**
	 * Compacts 8 booleans into a single byte
	 */
	COMPACT_BOOLEANS(true),

	/**
	 * Uses short but cryptic method names. Most method will be named "_"
	 */
	SHORT_METHOD_NAMES(true),
	/**
	 * Uses short but cryptic variable names. Most variables will be named "_"
	 */
	SHORT_VARIABLE_NAMES(true),

	/**
	 * Use a much faster allocation method for a serializer. <br>
	 * Might bring issues if someone is messing with internals.
	 */
	FAST_ALLOC(true);

	public static final String STRING_ENCODING = "key.string.encoding";
	public static final String STRING_UTF8 = "value.string.utf8";
	public static final String STRING_UTF16 = "value.string.utf16";
	public static final String ARRAY_LENGTH_TYPE = "value.array.type";
	public static final String BYTE = "value.byte";
	public static final String SHORT = "value.short";
	public static final String INT = "value.int";

	public final boolean defaultValue;

	Options(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}
}
