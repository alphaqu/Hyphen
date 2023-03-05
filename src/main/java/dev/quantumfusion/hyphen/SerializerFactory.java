package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.def.*;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.scan.annotations.DataGlobalAnnotation;
import dev.quantumfusion.hyphen.scan.struct.ClassStruct;
import dev.quantumfusion.hyphen.scan.struct.Struct;

import java.lang.annotation.Annotation;
import java.nio.*;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

/**
 * The Factory where you create a {@link HyphenSerializer} <br>
 * <p>
 * If you are looking at the code, this is mostly a wrapper around {@link SerializerGenerator}
 * as this class requires a lot of documentation which takes up a lot of screen space.
 *
 * @param <IO> IO Class
 * @param <D>  Data Class
 */
public class SerializerFactory<IO extends IOInterface, D> {
	public final Class<IO> ioClass;
	public final Class<D> dataClass;
	private final EnumMap<Options, Boolean> options;
	private final Map<Class<?>, DynamicDefFactory> definitions;
	private final Map<Object, List<Annotation>> annotationProviders = new HashMap<>();

	private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	private String className = "HyphenSerializer";
	private Path exportPath = null;

	private SerializerFactory(Class<IO> ioClass, Class<D> dataClass, boolean debug) {
		this.ioClass = ioClass;
		this.dataClass = dataClass;
		this.definitions = new HashMap<>(BUILD_IN_DEFINITIONS);
		this.options = new EnumMap<>(Options.class);
		for (Options value : Options.values()) {
			this.options.put(value, value.defaultValue);
		}
		if (debug) {
			this.options.put(Options.SHORT_METHOD_NAMES, false);
			this.options.put(Options.SHORT_VARIABLE_NAMES, false);
		}
	}

	// ======================================== CREATE ========================================

	/**
	 * Create a SerializerFactory which creates a HyphenSerializer with the preferred {@link IOInterface} and with your intended dataclass.<br>
	 * Please note that the resulting serializer is static, and you cannot change anything after creation.
	 *
	 * @param ioClass   The Data Handling Class
	 * @param dataClass The Data Class
	 * @param <IO>      IO
	 * @param <D>       DataClass
	 * @return A SerializerFactory
	 */
	public static <IO extends IOInterface, D> SerializerFactory<IO, D> create(Class<IO> ioClass, Class<D> dataClass) {
		return new SerializerFactory<>(ioClass, dataClass, false);
	}

	/**
	 * Create a SerializerFactory which creates a HyphenSerializer with the preferred {@link IOInterface} and with your intended dataclass.<br>
	 * Please note that the resulting serializer is static, and you cannot change anything after creation. <br><br>
	 * <p>
	 * This is the debug type with {@link Options#SHORT_METHOD_NAMES} and {@link Options#SHORT_VARIABLE_NAMES} turned off
	 *
	 * @param ioClass   The Data Handling Class
	 * @param dataClass The Data Class
	 * @param <IO>      IO
	 * @param <D>       DataClass
	 * @return A SerializerFactory
	 */
	public static <IO extends IOInterface, D> SerializerFactory<IO, D> createDebug(Class<IO> ioClass, Class<D> dataClass) {
		return new SerializerFactory<>(ioClass, dataClass, true);
	}

	// ======================================== OPTIONS ========================================

	/**
	 * This sets an optional option for the created {@link HyphenSerializer} <br>
	 * Anything you change here will be hardcoded into the serializer as it changes the bytecode
	 *
	 * @param option Any option from {@link Options}
	 * @param value  The option on state
	 */
	public void setOption(Options option, Boolean value) {
		this.options.put(option, value);
	}

	/**
	 * This sets the classloader used to define a {@link HyphenSerializer} <br>
	 * If this is not set the default classloader will be the thread classloader at factory creation.
	 *
	 * @param classLoader The intended classloader
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Sets the class name of the output {@link HyphenSerializer}
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * Sets the FILE location of the serializer. This will export the {@link HyphenSerializer} bytecode into the file. <br>
	 * <br>
	 *
	 * @see SerializerFactory#setExportDir
	 */
	public void setExportPath(Path path) {
		this.exportPath = path;
	}

	/**
	 * Sets the DIRECTION location of the serializer. This will export the {@link HyphenSerializer} bytecode into a file that matches the serializer name. <br>
	 * <br>
	 *
	 * @see SerializerFactory#setExportPath
	 */
	public void setExportDir(Path path) {
		this.setExportPath(path.resolve(this.className + ".class"));
	}

	// ====================================== DEFINITIONS =====================================

	/**
	 * This adds a static definition that does not change dependent on types or any other variables. <br> <br>
	 *
	 * @param target The Class Target to add a static definition to
	 * @param def    The Serializer Definition
	 * @see SerializerFactory#addDynamicDef(Class, DynamicDefFactory)
	 */
	public void addStaticDef(Class<?> target, SerializerDef def) {
		this.definitions.put(target, (clazz) -> def);
	}

	/**
	 * This adds a dynamic definition that does depend on the field itself. <br> <br>
	 *
	 * @param target  The Class Target to add a static definition to
	 * @param factory The Factory that creates a {@link SerializerDef}
	 * @see SerializerFactory#addStaticDef(Class, SerializerDef) (Class, DynamicDefFactory)
	 */
	public void addDynamicDef(Class<?> target, DynamicDefFactory factory) {
		this.definitions.put(target, factory);
	}

	// ====================================== ANNOTATIONS =====================================

	/**
	 * This adds annotations to the global annotation id. If a {@link DataGlobalAnnotation#value()}
	 * contains this {@code id} it will apply this and the previous annotations that were added to this id
	 *
	 * @param id         The {@link DataGlobalAnnotation#value()} that is targeted
	 * @param annotation The Annotation you are adding to the applications
	 */
	public void addAnnotationProvider(String id, Annotation annotation) {
		addAnnotationProviderInternal(id, annotation);
	}

	/**
	 * This adds annotations to the global annotation which targets a class. If a {@link DataGlobalAnnotation}
	 * is applied to a {@code clazz} field. it will apply this and the previous annotations that were added to this {@code clazz}
	 *
	 * @param clazz      The {@link DataGlobalAnnotation} field class that is targeted
	 * @param annotation The Annotation you are adding to the applications
	 */
	public void addAnnotationProvider(Class<?> clazz, Annotation annotation) {
		addAnnotationProviderInternal(clazz, annotation);
	}

	private void addAnnotationProviderInternal(Object id, Annotation annotation) {
		this.annotationProviders.computeIfAbsent(id, s -> new ArrayList<>()).add(annotation);
	}

	/**
	 * Builds a {@link HyphenSerializer}. Any options that are set at this point will be applied to the final {@link HyphenSerializer}
	 *
	 * @return A Serializer Powered by Hyphen.
	 */
	public HyphenSerializer<IO, D> build() {
		return new SerializerGenerator<>(ioClass, dataClass, className, exportPath, classLoader, options, definitions, annotationProviders).build();
	}


	/**
	 * This is a Dynamic Definition Factory that will create a Definition dependent on the Field itself.
	 */
	@FunctionalInterface
	public interface DynamicDefFactory {
		/**
		 * Create a SerializerDef dependant on the field
		 *
		 * @param struct             The Structure that the field is. Read more at {@link ClassStruct}
		 * @return SerializerDef
		 */
		SerializerDef<?> create(Struct struct);
	}

	private static final Map<Class<?>, DynamicDefFactory> BUILD_IN_DEFINITIONS = new HashMap<>();
	static {

		addStaticDef(PrimitiveIODef::new,
				boolean.class, byte.class, short.class, char.class, int.class, float.class, long.class, double.class);
		addDynamicDef(PrimitiveArrayIODef::new,
				boolean[].class, byte[].class, short[].class, char[].class, int[].class, float[].class, long[].class, double[].class);
		addDynamicDef(BufferDef::new, ByteBuffer.class, ShortBuffer.class, CharBuffer.class, IntBuffer.class, FloatBuffer.class, LongBuffer.class, DoubleBuffer.class);
		addStaticDef(BoxedIODef::new, Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Float.class, Long.class, Double.class);
		BUILD_IN_DEFINITIONS.put(String.class, (c) -> new StringIODef());
		BUILD_IN_DEFINITIONS.put(List.class, ListDef::new);
		BUILD_IN_DEFINITIONS.put(Map.class, MapDef::new);
		BUILD_IN_DEFINITIONS.put(Set.class, SetDef::new);
	}

	private static void addStaticDef(Function<Class<?>, SerializerDef<?>> creator, Class<?>... clazz) {
		for (Class<?> aClass : clazz) {
			BUILD_IN_DEFINITIONS.put(aClass, (sh) -> creator.apply(aClass));
		}
	}

	private static void addDynamicDef(DynamicDefFactory creator, Class<?>... clazz) {
		for (Class<?> aClass : clazz) {
			BUILD_IN_DEFINITIONS.put(aClass, creator);
		}
	}
}
