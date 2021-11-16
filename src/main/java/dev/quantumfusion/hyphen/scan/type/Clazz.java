package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.scan.Direction;
import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.thr.HyphenException;
import dev.quantumfusion.hyphen.util.ClassCache;
import dev.quantumfusion.hyphen.util.ScanUtil;
import dev.quantumfusion.hyphen.util.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.*;

/**
 * This is a basic Class which is a class and holds annotations.
 * There are multiple implementations of this and all are listed in the {@link dev.quantumfusion.hyphen.scan.type} package.
 * <br> <br>
 * Any Clazz instance may be an {@link UnknownClazz#UNKNOWN} which is when Hyphen cannot find out what the class is.
 * <p>
 * {@link ArrayClazz} An Array. It may be an array or a Type Array which comes from a parameter. <br>
 * {@link ParaClazz} A Parameterised class. This is a class which holds class parameters. <br>
 * These parameters may be {@link UnknownClazz#UNKNOWN} and that means they were unable to be determended
 * {@link TypeClazz} A Type Class. This is a class which comes from a Type of the parent class.
 * To get its actual clazz use {@link TypeClazz#getDefinedClass()} <br>
 * {@link UnknownClazz} An Unknown class that may be caused of using wildcards or raw types.
 */
public class Clazz {
	@NotNull
	public final Class<?> aClass;
	protected final SerializerHandler<?, ?> handler;
	// Object is the value
	protected final Map<Class<? extends Annotation>, Object> annotations;

	protected Clazz(SerializerHandler<?, ?> handler, @NotNull Class<?> aClass, Map<Class<? extends Annotation>, Object> annotations) {
		this.aClass = aClass;
		this.handler = handler;
		this.annotations = annotations;
	}

	public Clazz(SerializerHandler<?, ?> handler, @NotNull Class<?> aClass) {
		this.aClass = aClass;
		this.handler = handler;
		var map = new HashMap<Class<? extends Annotation>, Object>();
		ScanUtil.addAnnotations(aClass, map);
		this.annotations = map;
	}

	public static Clazz create(SerializerHandler<?, ?> handler, AnnotatedType type, @Nullable Clazz ctx) {
		return new Clazz(handler, (Class<?>) type.getType(), ScanUtil.acquireAnnotations(handler, type, ctx));
	}

	/**
	 * Get an annotation value. To see if the value is null or the annotation does not exist use
	 * {@link Clazz#containsAnnotation(Class)} before using this
	 *
	 * @param aClass The Annotation class
	 * @return The value. If the annotation does not hold a value the value might be {@code null}
	 */
	public Object getAnnotationValue(Class<? extends Annotation> aClass) {
		return annotations.get(aClass);
	}

	/**
	 * Checks if the Annotation exists on this clazz.
	 *
	 * @param aClass The Annotation class
	 * @return if it exists on this clazz
	 */
	public boolean containsAnnotation(Class<? extends Annotation> aClass) {
		return annotations.containsKey(aClass);
	}

	/**
	 * Gets the class that may have been tracked across parameters and types. This is the class you want to encode/decode.
	 *
	 * @return The Defined class
	 */

	public Class<?> getDefinedClass() {
		return aClass;
	}

	/**
	 * Gets the class that is in actual bytecode. In the case of types it may be {@link Object} but it may also be the bound.
	 *
	 * @return The Bytecode class
	 */
	public Class<?> getBytecodeClass() {
		return aClass;
	}

	/**
	 * Tries to define a type.
	 *
	 * @param typeName The Class Type name
	 * @return The Possible Clazz. Else {@link UnknownClazz#UNKNOWN}
	 */
	public Clazz define(String typeName) {
		return UnknownClazz.UNKNOWN;
	}

	public Clazz asSub(Class<?> sub) {
		final AnnotatedType[] path = ScanUtil.findPath(ScanUtil.wrap(sub), (test) -> ScanUtil.getClassFrom(test) == aClass, clazz -> ClassCache.getInherited(ScanUtil.getClassFrom(clazz)));
		if (path == null)
			throw new RuntimeException(sub.getSimpleName() + " does not inherit " + aClass.getSimpleName());

		var ctx = this;
		for (int i = path.length - 1; i >= 0; i--) {
			ctx = Clazzifier.create(handler, path[i], ctx, Direction.SUB);
		}

		return ctx;
	}

	public List<FieldEntry> getFields() {
		List<FieldEntry> fieldEntries = new ArrayList<>();
		final AnnotatedType aSuper = ClassCache.getSuperClass(aClass);
		if (aSuper != null)
			fieldEntries.addAll(Clazzifier.create(handler, aSuper, this, Direction.SUPER).getFields());


		for (var field : ClassCache.getFields(aClass)) {
			try {
				fieldEntries.add(new FieldEntry(field.field(), Clazzifier.create(handler, new ScanUtil.FieldAnnotatedType(field.field(), field.type()), this, Direction.NORMAL)));
			} catch (Throwable throwable) {
				throw HyphenException.thr("field", Style.LINE_RIGHT, field, throwable);
			}
		}

		return fieldEntries;
	}

	public int defined() {
		return 1;
	}

	@Override
	public String toString() {
		StringJoiner annotationJoiner = new StringJoiner("_", "<", ">");
		this.annotations.forEach((aClass1, value) -> {
			annotationJoiner.add('@' + aClass1.getSimpleName() + value);
		});
		return aClass.getSimpleName() + "_" + annotationJoiner;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Clazz clazz = (Clazz) o;
		return aClass.equals(clazz.aClass) && Objects.equals(annotations, clazz.annotations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(aClass, annotations);
	}
}
