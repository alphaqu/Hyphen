package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.io.IOInterface;

import java.util.EnumMap;

public class CodegenHandler<IO extends IOInterface, D> {
	// Settings
	private final Class<IO> ioClass;
	private final Class<D> dataClass;
	private final boolean debug;

	// Options
	private final EnumMap<Options, Boolean> options;

	public CodegenHandler(Class<IO> ioClass, Class<D> dataClass, boolean debug, EnumMap<Options, Boolean> options) {
		this.ioClass = ioClass;
		this.dataClass = dataClass;
		this.debug = debug;
		this.options = options;
	}

	public HyphenSerializer<IO, D> build() {
		return null;
	}
}
