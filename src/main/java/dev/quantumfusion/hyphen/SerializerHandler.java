package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.def.*;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.scan.type.ArrayClazz;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * The Actual generation logic. For usage use {@link SerializerFactory} instead.
 *
 * @param <IO> IO Class
 * @param <D>  Data Class
 */
public class SerializerHandler<IO extends IOInterface, D> {

	// Options, Shares with codegenHandler

	public final EnumMap<Options, Boolean> options;
	public final CodegenHandler<IO, D> codegenHandler;

	public final Map<Class<?>, SerializerFactory.DynamicDefCreator> definitions;
	public final Map<Clazz, SerializerDef> scanDeduplicationMap = new HashMap<>();

	public SerializerHandler(Class<IO> ioClass, Class<D> dataClass, boolean debug) {
		// Initialize options
		this.options = new EnumMap<>(Options.class);
		for (Options value : Options.values()) this.options.put(value, value.defaultValue);

		this.codegenHandler = new CodegenHandler<>(ioClass, dataClass, debug, options);
		this.definitions = new HashMap<>(BUILD_IN_DEFINITIONS);
	}

	public SerializerDef acquireDef(Clazz clazz) {
		if (scanDeduplicationMap.containsKey(clazz))
			return scanDeduplicationMap.get(clazz);

		var out = acquireDefNew(clazz);
		scanDeduplicationMap.put(clazz, out);
		return out;
	}

	private SerializerDef acquireDefNew(Clazz clazz) {
		var definedClass = clazz.getDefinedClass();

		//TODO Discuss about inherited definitions
		if (definitions.containsKey(definedClass)) {
			return definitions.get(definedClass).create(clazz, this);
		}

		if (clazz instanceof ArrayClazz arrayClazz)
			return new ArrayDef(this, arrayClazz);
		else return new ClassDef(this, clazz);
	}

	private static final Map<Class<?>, SerializerFactory.DynamicDefCreator> BUILD_IN_DEFINITIONS = new HashMap<>();

	static {
		addStaticDef(boolean.class, new IODef(boolean.class));
		addStaticDef(byte.class, new IODef(byte.class));
		addStaticDef(short.class, new IODef(short.class));
		addStaticDef(char.class, new IODef(char.class));
		addStaticDef(int.class, new IODef(int.class));
		addStaticDef(float.class, new IODef(float.class));
		addStaticDef(long.class, new IODef(long.class));
		addStaticDef(double.class, new IODef(double.class));
		addStaticDef(Boolean.class, new BoxedIODef(Boolean.class));
		addStaticDef(Byte.class, new BoxedIODef(Byte.class));
		addStaticDef(Short.class, new BoxedIODef(Short.class));
		addStaticDef(Character.class, new BoxedIODef(Character.class));
		addStaticDef(Integer.class, new BoxedIODef(Integer.class));
		addStaticDef(Float.class, new BoxedIODef(Float.class));
		addStaticDef(Long.class, new BoxedIODef(Long.class));
		addStaticDef(Double.class, new BoxedIODef(Double.class));

		addStaticDef(boolean[].class, new IODef(boolean[].class));
		addStaticDef(byte[].class, new IODef(byte[].class));
		addStaticDef(short[].class, new IODef(short[].class));
		addStaticDef(char[].class, new IODef(char[].class));
		addStaticDef(int[].class, new IODef(int[].class));
		addStaticDef(float[].class, new IODef(float[].class));
		addStaticDef(long[].class, new IODef(long[].class));
		addStaticDef(double[].class, new IODef(double[].class));

		addStaticDef(String.class, new StringIODef());
	}

	private static void addStaticDef(Class<?> clazz, SerializerDef def) {
		BUILD_IN_DEFINITIONS.put(clazz, (c, sh) -> def);
	}
}
