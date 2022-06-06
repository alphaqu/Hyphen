package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.scan.annotations.DataGlobalAnnotation;
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
		this.sh.options.put(option, value);
	}

	/**
	 * This sets the classloader used to define a {@link HyphenSerializer} <br>
	 * If this is not set the default classloader will be the thread classloader at factory creation.
	 *
	 * @param classLoader The intended classloader
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.sh.definer = new ClassDefiner(classLoader);
	}

	/**
	 * Sets the class name of the output {@link HyphenSerializer}
	 */
	public void setClassName(String name) {
		this.sh.name = name;
	}

	/**
	 * Sets the FILE location of the serializer. This will export the {@link HyphenSerializer} bytecode into the file. <br>
	 * <br>
	 *
	 * @see SerializerFactory#setExportDir
	 */
	public void setExportPath(Path path) {
		this.sh.exportPath = path;
	}

	/**
	 * Sets the DIRECTION location of the serializer. This will export the {@link HyphenSerializer} bytecode into a file that matches the serializer name. <br>
	 * <br>
	 *
	 * @see SerializerFactory#setExportPath
	 */
	public void setExportDir(Path path) {
		this.setExportPath(path.resolve(this.sh.name + ".class"));
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
		this.sh.definitions.put(target, (clazz, sh) -> def);
	}

	/**
	 * This adds a dynamic definition that does depend on the field itself. <br> <br>
	 *
	 * @param target  The Class Target to add a static definition to
	 * @param factory The Factory that creates a {@link SerializerDef}
	 * @see SerializerFactory#addStaticDef(Class, SerializerDef) (Class, DynamicDefFactory)
	 */
	public void addDynamicDef(Class<?> target, DynamicDefFactory factory) {
		this.sh.definitions.put(target, factory);
	}

	// ====================================== ANNOTATIONS =====================================

	/**
	 * This adds annotations to the global annotation id. If a {@link DataGlobalAnnotation#value()}
	 * contains this {@code id} it will apply this and the previous annotations that were added to this id
	 *
	 * @param id         The {@link DataGlobalAnnotation#value()} that is targeted
	 * @param annotation The Annotation you are adding to the applications
	 * @param value      The Annotation value. If the annotation does not contain a value this will be ignored. It may be {@code null}
	 */
	public void addGlobalAnnotation(String id, Class<? extends Annotation> annotation, Object value) {
		addGlobalAnnotationInternal(id, annotation, value);
	}

	/**
	 * This adds annotations to the global annotation which targets a class. If a {@link DataGlobalAnnotation}
	 * is applied to a {@code clazz} field. it will apply this and the previous annotations that were added to this {@code clazz}
	 *
	 * @param clazz      The {@link DataGlobalAnnotation} field class that is targeted
	 * @param annotation The Annotation you are adding to the applications
	 * @param value      The Annotation value. If the annotation does not contain a value this will be ignored. It may be {@code null}
	 */
	public void addGlobalAnnotation(Class<?> clazz, Class<? extends Annotation> annotation, Object value) {
		addGlobalAnnotationInternal(clazz, annotation, value);
	}

	private void addGlobalAnnotationInternal(Object id, Class<? extends Annotation> annotation, Object value) {
		var valueGetter = ScanUtil.getAnnotationValueGetter(annotation);
		if (valueGetter != null) {
			var returnType = valueGetter.getReturnType();
			var valueType = value.getClass();
			if (valueType != returnType) {
				throw new RuntimeException("Annotation " + annotation.getSimpleName() + " value type " + returnType.getSimpleName() + " does not match parameter " + valueType.getSimpleName());
			}
		}

		this.sh.globalAnnotations.computeIfAbsent(id, s -> new HashMap<>()).put(annotation, value);
	}

	/**
	 * Builds a {@link HyphenSerializer}. Any options that are set at this point will be applied to the final {@link HyphenSerializer}
	 *
	 * @return A Serializer Powered by Hyphen.
	 */
	public HyphenSerializer<IO, D> build() {
		return sh.build();
	}


	/**
	 * This is a Dynamic Definition Factory that will create a Definition dependent on the Field itself.
	 */
	@FunctionalInterface
	public interface DynamicDefFactory {
		/**
		 * Create a SerializerDef dependant on the field
		 *
		 * @param clazz             The Clazz that the field is. Read more at {@link Clazz}
		 * @param serializerHandler The SerializerHandler. Used to request another definition, as an example it may be used to serialize a parameter in the object.
		 * @return SerializerDef
		 */
		SerializerDef create(Clazz clazz, SerializerHandler<?, ?> serializerHandler);
	}
}
