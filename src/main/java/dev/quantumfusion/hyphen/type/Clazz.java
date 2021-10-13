package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Direction;
import dev.quantumfusion.hyphen.FieldEntry;
import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.annotations.Data;
import dev.quantumfusion.hyphen.util.ScanUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.*;

public class Clazz {
	@NotNull
	public final Class<?> aClass;
	public final Annotation[] annotations;

	public Clazz(@NotNull Class<?> aClass, Annotation[] annotations) {
		this.aClass = aClass;
		this.annotations = annotations;
	}

	public static Clazz create(AnnotatedType type, Clazz ctx, Direction dir) {
		return new Clazz((Class<?>) type.getType(), type.getDeclaredAnnotations());
	}


	public Clazz define(String typeName) {
		return ScanHandler.UNKNOWN;
	}

	public List<FieldEntry> asSub(Class<?> sub) {
		final AnnotatedType[] path = ScanUtil.findPath(ScanUtil.wrap(sub), (test) -> ScanUtil.getClassFrom(test) == aClass, ScanUtil::getInherited);
		if (path == null)
			throw new RuntimeException(sub.getSimpleName() + " does not inherit " + aClass.getSimpleName());

		var fields = new LinkedHashMap<Field, Clazz>();
		var ctx = this;
		for (int i = path.length - 1; i >= 0; i--) {
			ctx = ScanHandler.create(path[i], ctx, Direction.SUB);
			for (var field : ctx.getFields()) {
				var f = field.field();
				var c = field.clazz();
				if (fields.containsKey(f) && (fields.get(f).defined() > c.defined())) continue;
				fields.put(f, c);
			}
		}

		var out = new ArrayList<FieldEntry>();
		fields.forEach((field, clazz) -> out.add(new FieldEntry(clazz, field)));
		return out;
	}

	public List<FieldEntry> getFields() {
		List<FieldEntry> fieldEntries = new ArrayList<>();
		if (aClass.getSuperclass() != null)
			fieldEntries.addAll(ScanHandler.create(aClass.getAnnotatedSuperclass(), this, Direction.SUPER).getFields());

		for (Field field : aClass.getDeclaredFields()) {
			if (field.getAnnotatedType().getDeclaredAnnotation(Data.class) != null) {
				fieldEntries.add(new FieldEntry(ScanHandler.create(field.getAnnotatedType(), this, Direction.NORMAL), field));
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
