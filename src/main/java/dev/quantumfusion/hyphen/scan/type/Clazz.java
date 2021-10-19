package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.Annotations;
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
	private final Annotations annotations;

	protected Clazz(@NotNull Class<?> aClass, Annotations annotations) {
		this.aClass = aClass;
		this.annotations = annotations;
	}

	public Clazz(@NotNull Class<?> aClass) {
		this.aClass = aClass;
		this.annotations = Annotations.empty();
	}

	public static Clazz create(AnnotatedType type, @Nullable Clazz ctx) {
		return new Clazz((Class<?>) type.getType(), Annotations.of(type, ctx));
	}

	public <A extends Annotation> A getAnnotation(Class<? extends A> aClass) {
		return this.annotations.get(aClass);
	}

	public boolean containsAnnotation(Class<? extends Annotation> aClass) {
		return this.annotations.containsKey(aClass);
	}

	public Class<?> getDefinedClass() {
		return this.aClass;
	}

	public Class<?> getBytecodeClass() {
		return this.aClass;
	}

	public Clazz define(String typeName) {
		return UnknownClazz.UNKNOWN;
	}

	public Clazz asSub(Class<?> sub) {
		final AnnotatedType[] path = ScanUtil.findPath(ScanUtil.wrap(sub), (test) -> ScanUtil.getClassFrom(test) == this.aClass, clazz -> ClassCache.getInherited(ScanUtil.getClassFrom(clazz)));
		if (path == null)
			throw new RuntimeException(sub.getSimpleName() + " does not inherit " + this.aClass.getSimpleName());

		var ctx = this;
		for (int i = path.length - 1; i >= 0; i--) {
			ctx = Clazzifier.create(path[i], ctx, Direction.SUB);
		}

		return ctx;
	}

	public List<FieldEntry> getFields() {
		List<FieldEntry> fieldEntries = new ArrayList<>();
		final AnnotatedType aSuper = ClassCache.getSuperClass(this.aClass);
		if (aSuper != null)
			fieldEntries.addAll(Clazzifier.create(aSuper, this, Direction.SUPER).getFields());


		for (var field : ClassCache.getFields(this.aClass)) {
			try {
				fieldEntries.add(new FieldEntry(field.field(), Clazzifier.create(field.type(), this, Direction.NORMAL)));
			} catch (Throwable throwable) {
				throw HyphenException.thr("field", Style.LINE_RIGHT , field, throwable);
			}
		}

		return fieldEntries;
	}

	public int defined() {
		return 1;
	}

	@Override
	public String toString() {
		return this.annotations + this.aClass.getSimpleName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		Clazz clazz = (Clazz) o;
		return this.aClass.equals(clazz.aClass) && Objects.equals(this.annotations, clazz.annotations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.aClass, this.annotations);
	}
}
