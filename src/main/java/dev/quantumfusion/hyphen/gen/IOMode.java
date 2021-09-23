package dev.quantumfusion.hyphen.gen;

import dev.quantumfusion.hyphen.io.ArrayIO;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.io.UnsafeIO;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public enum IOMode {
	/**
	 * Generate bytecode that accepts the ByteBufferIO directly.
	 */
	BYTEBUFFER(ByteBufferIO.class),
	/**
	 * Generate bytecode that accepts the UnsafeIO directly.
	 */
	UNSAFE(UnsafeIO.class),
	/**
	 * Generate bytecode that accepts the ArrayIO directly.
	 */
	ARRAY(ArrayIO.class),
	/**
	 * Generate bytecode that accepts the IOInterface interface. For custom implementations.
	 */
	CUSTOM(IOInterface.class);


	public final Class<?> ioClass;

	IOMode(Class<?> ioClass) {
		this.ioClass = ioClass;
	}


	public void callMethod(MethodVisitor mv, String name, String descriptor) {
		mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(ioClass), name, descriptor, false);
	}
}
