package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.gen.CodegenHandler;
import dev.quantumfusion.hyphen.gen.SerializerDef;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.scan.ScanHandler;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.scan.type.Clz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * The Internal Serializer Creator
 */
public class SerializerHandler {
	public final Map<Class<?>, Function<Clazz, SerializerDef>> definitions = new HashMap<>();

	/**
	 * Settings
	 */
	private final Class<?> ioClass;
	private final Class<?> dataClass;
	private final String serializerName;
	private final boolean debug;


	/**
	 * Internal Handlers.
	 */
	public final CodegenHandler codegenHandler;
	public final ScanHandler scanHandler = new ScanHandler();

	public SerializerHandler(Class<? extends IOInterface> ioClass, Class<?> dataClass, String serializerName, boolean debug) {
		this.ioClass = ioClass;
		this.dataClass = dataClass;
		this.serializerName = serializerName;
		this.debug = debug;
		this.codegenHandler =  new CodegenHandler(serializerName, debug, ioClass, new ArrayList<>());
	}

	public SerializerDef acquireDef(Clz clazz) {
		return null;
	}
}
