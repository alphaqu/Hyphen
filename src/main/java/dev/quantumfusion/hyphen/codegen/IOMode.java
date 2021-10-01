package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.util.IOUtil;
import org.objectweb.asm.Type;

public class IOMode {
	public final Class<?> ioClass;
	public final String internalName;

	private IOMode(Class<?> ioClass, String internalName) {
		this.ioClass = ioClass;
		this.internalName = internalName;
	}

	public static IOMode create(Class<?> ioClass) {
		try {
			IOUtil.checkIOImpl(ioClass);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

		return new IOMode(ioClass, Type.getInternalName(ioClass));
	}
}
