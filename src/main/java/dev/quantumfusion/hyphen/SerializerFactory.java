package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.util.ScanUtil;

import java.lang.annotation.Annotation;
import java.util.HashMap;

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

	public void setClassLoader(ClassLoader classLoader) {
		this.serializerHandler.definer = new ClassDefiner(classLoader);
	}

	// ====================================== DEFINITIONS =====================================
	public void addStaticDef(Class<?> target, SerializerDef def) {
		this.serializerHandler.definitions.put(target, (clazz, sh) -> def);
	}

	public void addDynamicDef(Class<?> target, DynamicDefCreator defCreator) {
		this.serializerHandler.definitions.put(target, defCreator);
	}

	// ====================================== ANNOTATIONS =====================================
	public void addGlobalAnnotation(String id, Class<? extends Annotation> annotation, Object value) {
		var valueGetter = ScanUtil.getAnnotationValueGetter(annotation);
		if (valueGetter != null) {
			var returnType = valueGetter.getReturnType();
			var valueType = value.getClass();
			if (valueType != returnType)
				throw new RuntimeException("Annotation " + annotation.getSimpleName() + " value type " + returnType.getSimpleName() + " does not match parameter " + valueType.getSimpleName());
		}

		this.serializerHandler.globalAnnotations.computeIfAbsent(id, s -> new HashMap<>()).put(annotation, value);
	}

	public HyphenSerializer<IO, D> build() {
		return serializerHandler.build();
	}


	@FunctionalInterface
	public interface DynamicDefCreator {
		SerializerDef create(Clazz clazz, SerializerHandler<?, ?> serializerHandler);
	}
}
