package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.io.IOInterface;

public class CodegenHandler<IO extends IOInterface, D> {
	// Settings
	private final Class<IO> ioClass;
	private final Class<D> dataClass;
	private final boolean debug;

	public CodegenHandler(Class<IO> ioClass, Class<D> dataClass, boolean debug) {
		this.ioClass = ioClass;
		this.dataClass = dataClass;
		this.debug = debug;
	}

	public HyphenSerializer<IO, D> build() {
		return null;
	}
}
