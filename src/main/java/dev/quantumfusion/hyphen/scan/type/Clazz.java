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

public class Clazz {
	@NotNull
	public final Class<?> aClass;
	protected final SerializerHandler<?, ?> handler;
	// Object is the value
	private final Map<Class<? extends Annotation>, Object> annotations;

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

	public Object getAnnotationValue(Class<? extends Annotation> aClass) {
		return annotations.get(aClass);
	}

	public boolean containsAnnotation(Class<? extends Annotation> aClass) {
		return annotations.containsKey(aClass);
	}

	public Class<?> getDefinedClass() {
		return aClass;
	}

	public Class<?> getBytecodeClass() {
		return aClass;
	}

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
		StringJoiner annotationJoiner = new StringJoiner(" ", "<", ">");
		this.annotations.forEach((aClass1, value) -> {
			annotationJoiner.add('@' + aClass1.getSimpleName() + hashCode());
		});
		return aClass.getSimpleName() + " " + annotationJoiner;
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
