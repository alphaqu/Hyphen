package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.gen.SerializerDef;
import dev.quantumfusion.hyphen.scan.type.Clazz;

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

	public SerializerHandler(Class<?> ioClass, Class<?> dataClass, String serializerName) {
		this.ioClass = ioClass;
		this.dataClass = dataClass;
		this.serializerName = serializerName;
	}

	public SerializerDef acquireDef(Clazz clazz) {
		return null;
	}
}
