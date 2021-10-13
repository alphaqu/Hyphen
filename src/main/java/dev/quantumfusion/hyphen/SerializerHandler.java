package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.io.IOInterface;

import java.util.EnumMap;

/**
 * The Actual generation logic. For usage use {@link SerializerFactory} instead.
 *
 * @param <IO> IO Class
 * @param <D>  Data Class
 */
class SerializerHandler<IO extends IOInterface, D> {
	// Options, Shares with codegenHandler
	public final EnumMap<Options, Boolean> options;

	// Handlers
	public final CodegenHandler<IO, D> codegenHandler;

	public SerializerHandler(Class<IO> ioClass, Class<D> dataClass, boolean debug) {
		// Initialize options
		this.options = new EnumMap<>(Options.class);
		for (Options value : Options.values()) this.options.put(value, value.defaultValue);

		this.codegenHandler = new CodegenHandler<>(ioClass, dataClass, debug, options);
	}
}
