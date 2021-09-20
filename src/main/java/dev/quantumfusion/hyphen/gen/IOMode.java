package dev.quantumfusion.hyphen.gen;

public enum IOMode {
	/**
	 * Generate bytecode that accepts the ByteBufferIO directly.
	 */
	BYTEBUFFER,
	/**
	 * Generate bytecode that accepts the UnsafeIO directly.
	 */
	UNSAFE,
	/**
	 * Generate bytecode that accepts the ArrayIO directly.
	 */
	ARRAY,
	/**
	 * Generate bytecode that accepts the IOInterface interface. For custom implementations.
	 */
	CUSTOM
}
