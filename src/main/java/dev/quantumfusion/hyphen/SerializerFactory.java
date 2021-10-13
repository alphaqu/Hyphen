package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.io.IOInterface;

public class SerializerFactory<IO extends IOInterface, D> {
	// Internal Handlers
	private final CodegenHandler<IO, D> codegenHandler;

	private SerializerFactory(Class<IO> ioClass, Class<D> dataClass, boolean debug) {
		this.codegenHandler = new CodegenHandler<>(ioClass, dataClass, debug);
	}

	public static <IO extends IOInterface, D> SerializerFactory<IO, D> create(Class<IO> ioClass, Class<D> dataClass) {
		return new SerializerFactory<>(ioClass, dataClass, false);
	}

	public static <IO extends IOInterface, D> SerializerFactory<IO, D> createDebug(Class<IO> ioClass, Class<D> dataClass) {
		return new SerializerFactory<>(ioClass, dataClass, true);
	}

	public HyphenSerializer<IO, D> build() {
		return codegenHandler.build();
	}
}
