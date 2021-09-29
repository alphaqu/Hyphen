package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.IOHandler;
import dev.quantumfusion.hyphen.codegen.def.IODef;
import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.codegen.def.StaleDef;
import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.IllegalClassException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class SerializerFactory {
	private final Map<Class<?>, Function<? super TypeInfo, ? extends SerializerDef>> implementations = new HashMap<>();
	private final Map<TypeInfo, MethodMetadata> methods = new LinkedHashMap<>();
	private final Map<Object, List<Class<?>>> subclasses = new HashMap<>();
	private final boolean debug;
	private final Class<?> clazz;

	protected SerializerFactory(boolean debug, Class<?> clazz) {
		this.debug = debug;
		this.clazz = clazz;
	}

	public static SerializerFactory create(Class<?> clazz) {
		return createInternal(false, clazz);
	}

	public static SerializerFactory createDebug(Class<?> clazz) {
		return createInternal(true, clazz);
	}

	private static SerializerFactory createInternal(boolean debugMode, Class<?> clazz) {
		final SerializerFactory sh = new SerializerFactory(debugMode, clazz);
		sh.addImpl(IODef::new, boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class, double.class, String.class);
		sh.addImpl(IODef::new, boolean[].class, byte[].class, char[].class, short[].class, int[].class, float[].class, long[].class, double[].class, String[].class);
		sh.addImpl(List.class, new StaleDef());
		return sh;
	}

	public void addSubclasses(Class<?> clazz, Class<?>... subclass) {
		subclasses.computeIfAbsent(clazz, c -> new ArrayList<>()).addAll(Arrays.asList(subclass));
	}

	public void addSubclassKeys(String key, Class<?>... subclass) {
		subclasses.computeIfAbsent(key, c -> new ArrayList<>()).addAll(Arrays.asList(subclass));
	}

	public void addImpl(Class<?> clazz, Function<? super TypeInfo, ? extends SerializerDef> creator) {
		this.implementations.put(clazz, creator);
	}

	public void addImpl(Class<?> clazz, SerializerDef def) {
		this.implementations.put(clazz, (ti) -> def);
	}

	public void addImpl(SerializerDef... defs) {
		for (SerializerDef def : defs)
			this.implementations.put(def.getType(), (f) -> def);
	}

	public void addImpl(Function<Class<?>, SerializerDef> creator, Class<?>... classes) {
		for (Class<?> aClass : classes) {
			final SerializerDef def = creator.apply(aClass);
			this.implementations.put(def.getType(), (f) -> def);
		}
	}

	private static final boolean EXPORT = true;
	private static final String uwuSerializer = "UWUSerializer";

	public Class<?> build() {
		if (clazz.getTypeParameters().length > 0) {
			throw ThrowHandler.fatal(IllegalClassException::new, "The Input class has Parameters,");
		}
		ScanHandler scanner = new ScanHandler(methods, implementations, subclasses, debug);
		scanner.scan(clazz);



		CodegenHandler handler = new CodegenHandler(IOHandler.UNSAFE, uwuSerializer);
		handler.createConstructor();
		methods.forEach(handler::createEncode);
		methods.forEach(handler::createDecode);

		if(EXPORT){
			try {
				Files.write(Path.of("./" + uwuSerializer + ".class"), handler.byteArray());
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		return handler.export();
	}


}
