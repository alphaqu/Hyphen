package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.SerializerFactory;
import net.oskarstrom.hyphen.util.Color;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;

public class ClassInfo extends TypeInfo implements Type {
	protected final SerializerFactory factory;

	public ClassInfo(Class<?> clazz, Map<Class<Annotation>, Object> annotations, SerializerFactory factory) {
		super(clazz, annotations);
		this.factory = factory;
	}

	private ClassInfo[] getSuperClasses(ClassInfo in, int depth) {
		Class<?> clazz = in.clazz;
		Class<?> superclass = clazz.getSuperclass();

		if (superclass == null)
			return new ClassInfo[depth];

		ClassInfo info = factory.createClassInfo(in, superclass, clazz.getGenericSuperclass(), clazz.getAnnotatedSuperclass());
		ClassInfo[] out = getSuperClasses(info, depth + 1);
		out[depth] = info;
		return out;
	}

	protected List<FieldMetadata> getFields(Predicate<? super Field> filter) {
		List<FieldMetadata> info = new ArrayList<>();
		for (Field declaredField : clazz.getDeclaredFields()) {
			if (filter.test(declaredField)) {
				Type genericType = declaredField.getGenericType();
				ClassInfo classInfo = factory.createClassInfo(this, declaredField.getType(), genericType, declaredField.getAnnotatedType());
				info.add(new FieldMetadata(classInfo, declaredField.getModifiers(), declaredField.getName(), genericType));
			}
		}
		return info;
	}

	//returns for things like constructors
	public Class<?> getRawClass() {
		return clazz;
	}

	public List<FieldMetadata> getAllFields(Predicate<Field> filter) {
		List<FieldMetadata> out = new ArrayList<>();
		for (ClassInfo superClass : getSuperClasses(this, 0)) {
			out.addAll(superClass.getFields(filter));
		}
		out.addAll(getFields(filter));
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
