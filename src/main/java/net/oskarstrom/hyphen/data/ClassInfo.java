package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.SerializerFactory;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.util.Color;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ClassInfo extends TypeInfo implements Type {
	protected final SerializerFactory factory;

	public ClassInfo(Class<?> clazz, Map<Class<Annotation>, Object> annotations, SerializerFactory factory) {
		super(clazz, annotations, factory);
		this.factory = factory;
	}


	public SerializerMetadata createMeta() {
		var methodMetadata = new ClassSerializerMetadata(this);
		factory.methods.put(this, methodMetadata);

		if (factory.implementations.containsKey(this.clazz)) {
			methodMetadata.fields.put(null, factory.implementations.get(this.clazz).apply(this));
			return methodMetadata;
		}

		//get the fields
		var allFields = this.getAllFields(field -> field.getDeclaredAnnotation(Serialize.class) != null);
		//check if it exists / if its accessible
		factory.checkConstructor(allFields, this);
		for (FieldMetadata fieldInfo : allFields) {
			var def = factory.getDefinition(fieldInfo, this);
			methodMetadata.fields.put(fieldInfo, def);
		}
		return methodMetadata;
	}

	private ClassInfo[] getSuperClasses(ClassInfo in, int depth) {
		Class<?> clazz = in.clazz;
		Class<?> superclass = clazz.getSuperclass();

		if (superclass == null)
			return new ClassInfo[depth];

		TypeInfo typeInfo = TypeInfo.create(factory, in, superclass, clazz.getGenericSuperclass(), clazz.getAnnotatedSuperclass());

		if (!(typeInfo instanceof ClassInfo info)) {
			// this should always return a class info, unless you put a `SubClasses` annotations on an extends clause
			throw new IllegalStateException("I think you put `@SubClasses` on a extends clause?");
		}

		ClassInfo[] out = getSuperClasses(info, depth + 1);
		out[depth] = info;
		return out;
	}

	protected List<FieldMetadata> getFields(Predicate<? super Field> filter) {
		List<FieldMetadata> info = new ArrayList<>();
		for (Field declaredField : clazz.getDeclaredFields()) {
			if (filter.test(declaredField)) {
				Type genericType = declaredField.getGenericType();
				TypeInfo classInfo = TypeInfo.create(factory, this, declaredField.getType(), genericType, declaredField.getAnnotatedType());
				info.add(new FieldMetadata(classInfo, declaredField.getModifiers(), declaredField.getName(), genericType));
			}
		}
		return info;
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
