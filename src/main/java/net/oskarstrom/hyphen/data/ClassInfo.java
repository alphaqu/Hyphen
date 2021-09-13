package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.SerializerFactory;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

public class ClassInfo implements Type {
	public final Class<?> clazz;
	public final Map<Class<Annotation>, Object> annotations;
	@Nullable
	public final Class<?>[] superClasses;
	protected final SerializerFactory factory;

	public ClassInfo(Class<?> clazz, Map<Class<Annotation>, Object> annotations, SerializerFactory factory) {
		this.clazz = clazz;
		this.superClasses = getSuperClasses(clazz.getSuperclass(), 0);
		this.annotations = annotations;
		this.factory = factory;
	}

	private Class<?>[] getSuperClasses(Class<?> in, int depth) {
		if (in == null)
			return new Class<?>[depth];

		Class<?>[] out = getSuperClasses(in.getSuperclass(), depth + 1);
		out[depth] = in;
		return out;
	}

	public List<FieldInfo> getAllFields(Function<Field, Boolean> filter) {
		List<FieldInfo> out = new ArrayList<>();
		for (Field declaredField : clazz.getDeclaredFields()) {
			if (filter.apply(declaredField)) {
				out.add(new FieldInfo(declaredField, false));
			}
		}
		if (superClasses != null) {
			for (Class<?> superClass : superClasses) {
				if (superClass != null) {
					for (Field declaredField : superClass.getDeclaredFields()) {
						if (filter.apply(declaredField)) {
							out.add(new FieldInfo(declaredField, true));
						}
					}
				}
			}
		}
		return out;
	}

	public String toFancyString() {
		return clazz.getSimpleName();
	}

	@Override
	public String toString() {
		return clazz.getSimpleName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ClassInfo classInfo)) return false;
		return Objects.equals(clazz, classInfo.clazz) && Objects.equals(annotations, classInfo.annotations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(clazz, annotations);
	}

	public ClassInfo copy() {
		return new ClassInfo(clazz, new HashMap<>(annotations), factory);
	}
}
