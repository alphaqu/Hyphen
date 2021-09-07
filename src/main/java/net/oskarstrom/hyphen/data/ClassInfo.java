package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.SerializerFactory;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ClassInfo implements Type {
	public Class<?> clazz;
	@Nullable
	public Class<?>[] superClasses;
	protected final SerializerFactory factory;

	public ClassInfo(Class<?> clazz, SerializerFactory factory) {
		this.clazz = clazz;
		this.superClasses = getSuperClasses(clazz, 0);
		this.factory = factory;
	}

	private Class<?>[] getSuperClasses(Class<?> in, int depth) {
		if (in == null) {
			return new Class<?>[depth];
		}

		Class<?> superclass = in.getSuperclass();
		Class<?>[] out = getSuperClasses(superclass, depth + 1);
		if (out != null) {
			out[depth] = in;
			return out;
		}

		return null;
	}

	public List<Field> getAllFields(Function<Field, Boolean> filter) {
		List<Field> out = new ArrayList<>();
		for (Field declaredField : clazz.getDeclaredFields()) {
			if (filter.apply(declaredField)) {
				out.add(declaredField);
			}
		}
		if (superClasses != null) {
			for (Class<?> superClass : superClasses) {
				if (superClass != null) {
					for (Field declaredField : superClass.getDeclaredFields()) {
						if (filter.apply(declaredField)) {
							out.add(declaredField);
						}
					}
				}
			}
		}
		return out;
	}

	@Override
	public String toString() {
		return clazz.getSimpleName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ClassInfo classInfo)) return false;
		return Objects.equals(clazz, classInfo.clazz);
	}

	@Override
	public int hashCode() {
		return Objects.hash(clazz);
	}
}
