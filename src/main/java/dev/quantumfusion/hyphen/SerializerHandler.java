package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.def.*;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.scan.type.ArrayClazz;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * The Actual generation logic. For usage use {@link SerializerFactory} instead.
 *
 * @param <IO> IO Class
 * @param <D>  Data Class
 */
public class SerializerHandler<IO extends IOInterface, D> {

	// Options, Shares with codegenHandler

	public final EnumMap<Options, Boolean> options;
	public final Class<D> dataClass;
	public final Class<IO> ioClass;
	public final boolean debug;
	public ClassDefiner definer = new ClassDefiner(Thread.currentThread().getContextClassLoader());

	public final Map<Class<?>, SerializerFactory.DynamicDefCreator> definitions;
	public final Map<Clazz, SerializerDef> scanDeduplicationMap = new HashMap<>();
	public final Map<Clazz, MethodDef> methods = new HashMap<>();
	// not null on export
	public CodegenHandler<IO, D> codegenHandler;

	public SerializerHandler(Class<IO> ioClass, Class<D> dataClass, boolean debug) {
		// Initialize options
		this.options = new EnumMap<>(Options.class);
		for (Options value : Options.values()) this.options.put(value, value.defaultValue);

		this.dataClass = dataClass;
		this.ioClass = ioClass;
		this.debug = debug;

		if (debug) {
			this.options.put(Options.SHORT_METHOD_NAMES, false);
			this.options.put(Options.SHORT_VARIABLE_NAMES, false);
		}

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
		if (definitions.containsKey(definedClass))
			return definitions.get(definedClass).create(clazz, this);

		final MethodDef methodDef = acquireDefNewMethod(clazz);
		methods.put(clazz, methodDef);
		return methodDef;
	}

	private MethodDef acquireDefNewMethod(Clazz clazz) {
		if (clazz instanceof ArrayClazz arrayClazz)
			return new ArrayDef(this, arrayClazz);
		else return new ClassDef(this, clazz);
	}

	private MethodDef scan() {
		return acquireDefNewMethod(new Clazz(dataClass));
	}

	public HyphenSerializer<IO, D> build() {
		this.codegenHandler = new CodegenHandler<>(ioClass, dataClass, debug, options, definer);
		codegenHandler.setupSpark(this.scan());
		codegenHandler.writeMethods(methods.values());
		return codegenHandler.export();
	}

	private static final Map<Class<?>, SerializerFactory.DynamicDefCreator> BUILD_IN_DEFINITIONS = new HashMap<>();

	static {
		addStaticDef(IODef::new,
				boolean.class, byte.class, short.class, char.class, int.class, float.class, long.class, double.class,
				boolean[].class, byte[].class, short[].class, char[].class, int[].class, float[].class, long[].class, double[].class);
		addStaticDef(BoxedIODef::new, Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Float.class, Long.class, Double.class);
		BUILD_IN_DEFINITIONS.put(String.class, (c, sh) -> new StringIODef());
	}

	private static void addStaticDef(Function<Class<?>, SerializerDef> creator, Class<?>... clazz) {
		for (Class<?> aClass : clazz) {
			BUILD_IN_DEFINITIONS.put(aClass, (c, sh) -> creator.apply(aClass));
		}
	}
}
