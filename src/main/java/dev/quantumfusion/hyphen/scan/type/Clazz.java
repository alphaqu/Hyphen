package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.scan.Direction;
import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.ScanUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
		final AnnotatedType[] path = ScanUtil.findPath(ScanUtil.wrap(sub), (test) -> ScanUtil.getClassFrom(test) == aClass, ScanUtil::getInherited);
		if (path == null)
			throw new RuntimeException(sub.getSimpleName() + " does not inherit " + aClass.getSimpleName());

		var fields = new LinkedHashMap<Field, Clazz>();
		var ctx = this;
		for (int i = path.length - 1; i >= 0; i--) {
			ctx = Clazzifier.create(path[i], ctx, Direction.SUB);
			for (var field : ctx.getFields()) {
				var f = field.field();
				var c = field.clazz();
				if (fields.containsKey(f) && (fields.get(f).defined() > c.defined())) continue;
				fields.put(f, c);
			}
		}

		return fields.entrySet().stream().map(FieldEntry::create).collect(Collectors.toList());
	}

	public List<FieldEntry> getFields() {
		List<FieldEntry> fieldEntries = new ArrayList<>();
		if (aClass.getSuperclass() != null)
			fieldEntries.addAll(Clazzifier.create(aClass.getAnnotatedSuperclass(), this, Direction.SUPER).getFields());

		for (Field field : aClass.getDeclaredFields()) {
			if (field.getAnnotatedType().getDeclaredAnnotation(Data.class) != null) {
				fieldEntries.add(new FieldEntry(field, Clazzifier.create(field.getAnnotatedType(), this, Direction.NORMAL)));
			}
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
}
