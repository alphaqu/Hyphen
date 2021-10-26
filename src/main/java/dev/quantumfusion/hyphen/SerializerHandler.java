package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.def.*;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.type.*;
import dev.quantumfusion.hyphen.thr.HyphenException;
import dev.quantumfusion.hyphen.thr.UnknownTypeException;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
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

	private static final Map<Class<?>, SerializerFactory.DynamicDefCreator> BUILD_IN_DEFINITIONS = new HashMap<>();

	static {
		addStaticDef(PrimitiveIODef::new,
					 boolean.class, byte.class, short.class, char.class, int.class, float.class, long.class, double.class);
		addStaticDef(PrimitiveArrayIODef::new,
					 boolean[].class, byte[].class, short[].class, char[].class, int[].class, float[].class, long[].class, double[].class);
		addStaticDef(BoxedIODef::new, Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Float.class, Long.class, Double.class);
		BUILD_IN_DEFINITIONS.put(String.class, (c, sh) -> new StringIODef());
		BUILD_IN_DEFINITIONS.put(List.class, (c, sh) -> new ListDef(sh, (ParaClazz) c));
		BUILD_IN_DEFINITIONS.put(Map.class, (c, sh) -> new MapDef(sh, (ParaClazz) c));
	}

	public final EnumMap<Options, Boolean> options;
	public final Class<D> dataClass;
	public final Class<IO> ioClass;
	public final boolean debug;
	public final Map<Class<?>, SerializerFactory.DynamicDefCreator> definitions;
	public final Map<String, Map<Class<? extends Annotation>, Object>> globalAnnotations;
	public final Map<Clazz, SerializerDef> scanDeduplicationMap = new HashMap<>();
	public final Map<Clazz, MethodDef> methods = new HashMap<>();
	public ClassDefiner definer = new ClassDefiner(Thread.currentThread().getContextClassLoader());
	// not null on export
	public CodegenHandler<IO, D> codegenHandler;

	private String name = "HyphenSerializer";
	private Path exportPath = null;

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
		this.globalAnnotations = new HashMap<>();
	}

	private static void addStaticDef(Function<Class<?>, SerializerDef> creator, Class<?>... clazz) {
		for (Class<?> aClass : clazz) {
			BUILD_IN_DEFINITIONS.put(aClass, (c, sh) -> creator.apply(aClass));
		}
	}

	public SerializerDef acquireDef(Clazz clazz) {
		checkDefined(clazz);

		if (clazz instanceof TypeClazz t)
			return this.acquireDef(t.getDefined());

		if (scanDeduplicationMap.containsKey(clazz))
			return scanDeduplicationMap.get(clazz);

		var out = acquireDefNew(clazz);
		if (out instanceof MethodDef methodDef)
			methods.put(clazz, methodDef);

		scanDeduplicationMap.put(clazz, out);
		return out;
	}

	private SerializerDef acquireDefNew(Clazz clazz) {
		var definedClass = clazz.getDefinedClass();
		//TODO Discuss about inherited definitions
		if (definitions.containsKey(definedClass))
			return definitions.get(definedClass).create(clazz, this);
		return this.acquireDefNewMethod(clazz);
	}

	private MethodDef acquireDefNewMethod(Clazz clazz) {
		if (clazz.containsAnnotation(DataSubclasses.class))
			return new SubclassDef(this, clazz, (Class<?>[]) clazz.getAnnotationValue(DataSubclasses.class));
		if (clazz instanceof ArrayClazz arrayClazz)
			return new ArrayDef(this, arrayClazz);
		if (clazz.getDefinedClass().isEnum())
			return new EnumDef(this, clazz);
		else return new ClassDef(this, clazz);
	}

	private void checkDefined(Clazz clazz) {
		if (clazz == UnknownClazz.UNKNOWN)
			throw new UnknownTypeException("Type could not be identified",
										   "Check the Path for the source of \"UNKNOWN\" which is when a type is not known");

		if ((clazz instanceof TypeClazz t && (t.defined == UnknownClazz.UNKNOWN))) {
			throw new UnknownTypeException("Type " + t.typeName + " could not be identified",
										   "Trace the path of \"" + t.typeName + "\" in the path below. And see if you can define that path.");
		}
	}

	private MethodDef scan() {
		return acquireDefNewMethod(new Clazz(this, dataClass));
	}

	/**
	 * Sets the name of the produced serializer class.
	 */
	public SerializerHandler<IO, D> withName(String name){
		this.name = name;
		return this;
	}

	/**
	 * Sets the file location to export to.
	 */
	public SerializerHandler<IO, D> withExportPath(Path path){
		this.exportPath = path;
		return this;
	}

	/**
	 * Sets the directory to export to. Uses {@link #withName(String)} to call {@link #withExportPath(Path)}
	 */
	public SerializerHandler<IO, D> withExportDir(Path path){
		return this.withExportPath(path.resolve(this.name + ".class"));
	}



	public HyphenSerializer<IO, D> build() {
		try {
			this.codegenHandler = new CodegenHandler<>(ioClass, dataClass, debug, this.name, options, definer);
			codegenHandler.setupSpark(this.scan());
			codegenHandler.writeMethods(methods.values());
			return codegenHandler.export(this.exportPath);
		} catch (Throwable throwable) {
			HyphenException hyphenException;
			if (throwable instanceof HyphenException he) hyphenException = he;
			else hyphenException = new HyphenException(throwable, null);
			throw hyphenException;
		}
	}
}
