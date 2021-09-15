package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.SerializerFactory;
import net.oskarstrom.hyphen.util.Color;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class ClassInfo extends TypeInfo implements Type {
	@Nullable
	private final Class<?>[] superClasses;
	protected final SerializerFactory factory;

	public ClassInfo(Class<?> clazz, Map<Class<Annotation>, Object> annotations, SerializerFactory factory) {
		super(clazz, annotations);
		this.superClasses = this.getSuperClasses(clazz.getSuperclass(), 0);
		this.factory = factory;
	}

	private Class<?>[] getSuperClasses(Class<?> in, int depth) {
		if (in == null)
			return new Class<?>[depth];

		Class<?>[] out = this.getSuperClasses(in.getSuperclass(), depth + 1);
		out[depth] = in;
		return out;
	}

	public List<FieldMetadata> getAllFields(Predicate<? super Field> filter) {
		List<FieldMetadata> out = new ArrayList<>();
		for (Field declaredField : this.clazz.getDeclaredFields()) {
			if (filter.test(declaredField)) {
				out.add(new FieldMetadata(declaredField, false));
			}
		}
		if (this.superClasses != null) {
			for (Class<?> superClass : this.superClasses) {
				if (superClass != null) {
					for (Field declaredField : superClass.getDeclaredFields()) {
						if (filter.test(declaredField)) {
							out.add(new FieldMetadata(declaredField, true));
						}
					}
				}
			}
		}
		return out;
	}

	@Override
	public String toFancyString() {
		return Color.YELLOW + this.clazz.getSimpleName();
	}

	@Override
	public String toString() {
		return this.clazz.getSimpleName();
	}

	@Override
	public ClassInfo copy() {
		return new ClassInfo(this.clazz, new HashMap<>(this.annotations), this.factory);
	}
}
