package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.scan.Direction;
import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.util.ClassCache;
import dev.quantumfusion.hyphen.util.ScanUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class Clazz {
	@NotNull
	public final Class<?> aClass;
	public final Annotation[] annotations;
	public final Annotation[] parentClassAnnotations;

	protected Clazz(@NotNull Class<?> aClass, Annotation[] sourceAnnotations, Annotation[] annotations) {
		this.aClass = aClass;
		this.annotations = annotations;
		this.parentClassAnnotations = sourceAnnotations;
	}

	public Clazz(@NotNull Class<?> aClass) {
		this.aClass = aClass;
		this.annotations = new Annotation[0];
		this.parentClassAnnotations = new Annotation[0];
	}

	public static Clazz create(AnnotatedType type, @Nullable Clazz ctx) {
		return new Clazz((Class<?>) type.getType(), ScanUtil.parseAnnotations(ctx), type.getDeclaredAnnotations());
	}

	public Class<?> getDefinedClass() {
		return aClass;
	}

	public Class<?> getBytecodeClass() {
		return aClass;
	}

	public Annotation[] getClassAnnotations() {
		return aClass.getDeclaredAnnotations();
	}

	public Clazz define(String typeName) {
		return UnknownClazz.UNKNOWN;
	}

	public List<FieldEntry> asSub(Class<?> sub) {
		final AnnotatedType[] path = ScanUtil.findPath(ScanUtil.wrap(sub), (test) -> ScanUtil.getClassFrom(test) == aClass, clazz -> ClassCache.getInherited(ScanUtil.getClassFrom(clazz)));
		if (path == null)
			throw new RuntimeException(sub.getSimpleName() + " does not inherit " + aClass.getSimpleName());

		var fields = new LinkedHashMap<Field, Clazz>();
		var ctx = this;
		for (int i = path.length - 1; i >= 0; i--) {
			ctx = Clazzifier.create(path[i], ctx, Direction.SUB);
			for (var field : ctx.getFields()) {
				var f = field.field();
				var c = field.clazz();
				if (!fields.containsKey(f) || (fields.get(f).defined() <= c.defined())) {
					fields.put(f, c);
				}
			}
		}

		return fields.entrySet().stream().map(FieldEntry::create).collect(Collectors.toList());
	}

	public List<FieldEntry> getFields() {
		List<FieldEntry> fieldEntries = new ArrayList<>();
		final AnnotatedType aSuper = ClassCache.getSuperClass(aClass);
		if (aSuper != null)
			fieldEntries.addAll(Clazzifier.create(aSuper, this, Direction.SUPER).getFields());


		for (var field : ClassCache.getFields(aClass)) {
			fieldEntries.add(new FieldEntry(field.field(), Clazzifier.create(field.type(), this, Direction.NORMAL)));
		}

		return fieldEntries;
	}

	public int defined() {
		return 1;
	}

	@Override
	public String toString() {
		return aClass.getSimpleName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Clazz clazz = (Clazz) o;
		return aClass.equals(clazz.aClass) && Arrays.equals(annotations, clazz.annotations) && Arrays.equals(parentClassAnnotations, clazz.parentClassAnnotations);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(aClass);
		result = 31 * result + Arrays.hashCode(annotations);
		result = 31 * result + Arrays.hashCode(parentClassAnnotations);
		return result;
	}
}
