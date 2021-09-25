package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.io.ArrayIO;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.io.UnsafeIO;
import org.objectweb.asm.Type;

public enum IOHandler {
	BYTEBUFFER(ByteBufferIO.class, false),
	/**
	 * Generate bytecode that accepts the UnsafeIO directly.
	 */
	UNSAFE(UnsafeIO.class, false),
	/**
	 * Generate bytecode that accepts the ArrayIO directly.
	 */
	ARRAY(ArrayIO.class, false),
	/**
	 * Generate bytecode that accepts the IOInterface interface. For custom implementations.
	 */
	CUSTOM(IOInterface.class, true);


	public final Class<?> ioClass;
	public final String internalName;
	public final boolean isInterface;

	IOHandler(Class<?> ioClass, boolean isInterface) {
		this.ioClass = ioClass;
		this.internalName = Type.getInternalName(ioClass);
		this.isInterface = isInterface;
	}

}
