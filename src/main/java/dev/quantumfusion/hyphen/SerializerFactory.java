package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.scan.type.Clazz;

/**
 * The Factory where you create a {@link HyphenSerializer} <br>
 * <p>
 * If you are looking at the code, this is mostly a wrapper around {@link SerializerHandler}
 * as this class requires a lot of documentation which takes up a lot of screen space.
 *
 * @param <IO> IO Class
 * @param <D>  Data Class
 */
public class SerializerFactory<IO extends IOInterface, D> {
	//For anyone reading. Here is the logic. This class is just documentation
	private final SerializerHandler<IO, D> serializerHandler;

	private SerializerFactory(Class<IO> ioClass, Class<D> dataClass, boolean debug) {
		this.serializerHandler = new SerializerHandler<>(ioClass, dataClass, debug);
	}

	// ======================================== CREATE ========================================
	public static <IO extends IOInterface, D> SerializerFactory<IO, D> create(Class<IO> ioClass, Class<D> dataClass) {
		return new SerializerFactory<>(ioClass, dataClass, false);
	}

	public static <IO extends IOInterface, D> SerializerFactory<IO, D> createDebug(Class<IO> ioClass, Class<D> dataClass) {
		return new SerializerFactory<>(ioClass, dataClass, true);
	}

	// ======================================== OPTION ========================================
	public void setOption(Options option, Boolean value) {
		this.serializerHandler.options.put(option, value);
	}

	// ====================================== DEFINITIONS =====================================
	//TODO add definitions
	public HyphenSerializer<IO, D> build() {
		return serializerHandler.codegenHandler.build();
	}


	@FunctionalInterface
	public interface DynamicDefCreator {
		SerializerDef create(Clazz clazz, SerializerHandler<?,?> serializerHandler);
	}
}
