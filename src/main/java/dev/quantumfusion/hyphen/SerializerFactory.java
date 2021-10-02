package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.def.*;
import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.IllegalClassException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class SerializerFactory<K> {
	private static final boolean EXPORT = true;
	private static final String uwuSerializer = "UWUSerializer";
	private final Map<Class<?>, Function<? super TypeInfo, ? extends SerializerDef>> implementations = new HashMap<>();
	private final Map<TypeInfo, MethodMetadata> methods = new LinkedHashMap<>();
	private final Map<Object, List<Class<?>>> subclasses = new HashMap<>();
	private final EnumMap<Options, Boolean> options = new EnumMap<>(Options.class);
	private final boolean debug;
	private final Class<?> clazz;

	protected SerializerFactory(boolean debug, Class<K> clazz) {
		this.debug = debug;
		this.clazz = clazz;
		for (Options value : Options.values()) this.options.put(value, value.defaultValue);
	}

	/**
	 * Creates a {@code SerializerFactory} for creating the serializer class.
	 * This factory holds the implementations and logic for creating the classes.
	 *
	 * <blockquote>
	 * {@code SerializerFactory.create(class).build()}
	 * </blockquote>
	 * <p>
	 * Where {@code class} is the class you are going to be serializing.
	 *
	 * <p> If you want to add a custom implementation you can do
	 *
	 * <blockquote>
	 * {@code SerializerFactory factory = SerializerFactory.create(Integer.class);}
	 * <p> {@code factory.addImpl(new CustomImpl());}</p>
	 * <p> {@code factory.build();}</p>
	 * </blockquote>
	 * </p>
	 *
	 * @param clazz The DataClass.
	 * @return the Factory in charge of creating the serializer.
	 */
	public static <K> SerializerFactory<K> create(Class<K> clazz) {
		return createInternal(false, clazz);
	}

	/**
	 * Create the {@code SerializerFactory} with debug features.
	 *
	 * @param clazz The DataClass.
	 * @return The {@code SerializerFactory}.
	 */
	public static <K> SerializerFactory<K> createDebug(Class<K> clazz) {
		final SerializerFactory<K> internal = createInternal(true, clazz);
		internal.setOption(Options.COMPACT_METHODS, false);
		internal.setOption(Options.COMPACT_VARIABLES, false);
		return internal;
	}

	private static <K> SerializerFactory<K> createInternal(boolean debugMode, Class<K> clazz) {
		final SerializerFactory<K> sh = new SerializerFactory<>(debugMode, clazz);
		sh.addImpl(IODef::new, boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class, double.class, String.class);
		sh.addImpl(new BoxedDef(Boolean.class, new IODef(boolean.class)), new BoxedDef(Byte.class, new IODef(byte.class)), new BoxedDef(Character.class, new IODef(char.class)), new BoxedDef(Short.class, new IODef(short.class)), new BoxedDef(Integer.class, new IODef(int.class)), new BoxedDef(Long.class, new IODef(long.class)), new BoxedDef(Float.class, new IODef(float.class)), new BoxedDef(Double.class, new IODef(double.class)));
		sh.addImpl(ArrayIODef::new, boolean[].class, byte[].class, char[].class, short[].class, int[].class, float[].class, long[].class, double[].class, String[].class);
		sh.addImpl(new StaleDef(List.class));
		return sh;
	}

	/**
	 * Adds all the subclasses.
	 *
	 * @param clazz      The Super Class
	 * @param subclasses The Subclasses
	 */
	public void addSubclasses(Class<?> clazz, Class<?>... subclasses) {
		this.subclasses.computeIfAbsent(clazz, c -> new ArrayList<>()).addAll(Arrays.asList(subclasses));
	}

	/**
	 * Adds all the subclasses to the key.
	 *
	 * @param key        The Annotation Key
	 * @param subclasses The Subclasses
	 */
	public void addSubclassKeys(String key, Class<?>... subclasses) {
		this.subclasses.computeIfAbsent(key, c -> new ArrayList<>()).addAll(Arrays.asList(subclasses));
	}

	/**
	 * Adds an {@link SerializerDef} that requires to be created on field scan. (to pull annotations and such)
	 *
	 * @param creator The Creator
	 * @param clazz   The Class
	 */
	public void addTypeImpl(Function<? super TypeInfo, ? extends SerializerDef> creator, Class<?> clazz) {
		this.implementations.put(clazz, creator);
	}

	/**
	 * Adds multiple static {@link SerializerDef}
	 *
	 * @param defs The Definitions.
	 */
	public void addImpl(SerializerDef... defs) {
		for (SerializerDef def : defs)
			this.implementations.put(def.getType(), (f) -> def);
	}

	/**
	 * Adds Multiple static {@link SerializerDef}
	 *
	 * @param creator The Classes Iterator that creates the {@link SerializerDef}
	 * @param classes The Classes
	 */
	public void addImpl(Function<Class<?>, SerializerDef> creator, Class<?>... classes) {
		for (Class<?> aClass : classes) {
			final SerializerDef def = creator.apply(aClass);
			this.implementations.put(def.getType(), (f) -> def);
		}
	}

	public void setOption(Options option, boolean value) {
		this.options.put(option, value);
	}

	/**
	 * Builds the Serializer Class
	 *
	 * @return The Serializer Class.
	 */
	public <IO> HyphenSerializer<K, IO> build(Class<IO> io) {
		if (clazz.getTypeParameters().length > 0) {
			throw ThrowHandler.fatal(IllegalClassException::new, "The Input class has Parameters,");
		}
		ScanHandler scanner = new ScanHandler(methods, implementations, subclasses, debug);
		scanner.scan(clazz);


		CodegenHandler handler = new CodegenHandler(options, methods, io, uwuSerializer);
		handler.createConstructor();
		handler.createMainMethods(scanner.mainSerializeMethod);
		handler.createMethods();
		if (EXPORT) {
			try {
				Files.write(Path.of("./" + uwuSerializer + ".class"), handler.byteArray());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		final Class<?> export = handler.export();
		try {
			return (HyphenSerializer<K, IO>) export.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}


}
