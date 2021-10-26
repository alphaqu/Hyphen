package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.util.ScanUtil;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
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
	private final SerializerHandler<IO, D> sh;

	private SerializerFactory(Class<IO> ioClass, Class<D> dataClass, boolean debug) {
		this.sh = new SerializerHandler<>(ioClass, dataClass, debug);
	}

	// ======================================== CREATE ========================================
	public static <IO extends IOInterface, D> SerializerFactory<IO, D> create(Class<IO> ioClass, Class<D> dataClass) {
		return new SerializerFactory<>(ioClass, dataClass, false);
	}

	public static <IO extends IOInterface, D> SerializerFactory<IO, D> createDebug(Class<IO> ioClass, Class<D> dataClass) {
		return new SerializerFactory<>(ioClass, dataClass, true);
	}

	// ======================================== OPTIONS ========================================
	public void setOption(Options option, Boolean value) {
		this.sh.options.put(option, value);
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.sh.definer = new ClassDefiner(classLoader);
	}

	/**
	 * Sets the name of the produced serializer class.
	 */
	public void setClassName(String name) {
		this.sh.name = name;
	}

	/**
	 * Sets the file location to export to.
	 */
	public void setExportPath(Path path) {
		this.sh.exportPath = path;
	}

	/**
	 * Sets the directory to export to. Uses {@link #setClassName(String)} to call {@link #setExportPath(Path)}
	 */
	public void setExportDir(Path path) {
		this.setExportPath(path.resolve(this.sh.name + ".class"));
	}

	// ====================================== DEFINITIONS =====================================
	public void addStaticDef(Class<?> target, SerializerDef def) {
		this.sh.definitions.put(target, (clazz, sh) -> def);
	}

	public void addDynamicDef(Class<?> target, DynamicDefCreator defCreator) {
		this.sh.definitions.put(target, defCreator);
	}

	// ====================================== ANNOTATIONS =====================================
	public void addGlobalAnnotation(String id, Class<? extends Annotation> annotation, Object value) {
		addGlobalAnnotationInternal(id, annotation, value);
	}

	public void addGlobalAnnotation(Class<?> clazz, Class<? extends Annotation> annotation, Object value) {
		addGlobalAnnotationInternal(clazz, annotation, value);
	}

	private void addGlobalAnnotationInternal(Object id, Class<? extends Annotation> annotation, Object value) {
		var valueGetter = ScanUtil.getAnnotationValueGetter(annotation);
		if (valueGetter != null) {
			var returnType = valueGetter.getReturnType();
			var valueType = value.getClass();
			if (valueType != returnType)
				throw new RuntimeException("Annotation " + annotation.getSimpleName() + " value type " + returnType.getSimpleName() + " does not match parameter " + valueType.getSimpleName());
		}

		this.sh.globalAnnotations.computeIfAbsent(id, s -> new HashMap<>()).put(annotation, value);
	}

	public HyphenSerializer<IO, D> build() {
		return sh.build();
	}


	@FunctionalInterface
	public interface DynamicDefCreator {
		SerializerDef create(Clazz clazz, SerializerHandler<?, ?> serializerHandler);
	}
}
