package dev.quantumfusion.hyphen.info;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.gen.metadata.ClassSerializerMetadata;
import dev.quantumfusion.hyphen.gen.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.HyphenException;
import dev.quantumfusion.hyphen.util.ScanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ClassInfo extends TypeInfo implements Type {
	private static final Map<ClassInfo, ClassInfo> dedupMap = new HashMap<>();
	private SerializerMetadata metadata;

	public ClassInfo(Class<?> clazz, Map<Class<Annotation>, Annotation> annotations) {
		super(clazz, annotations);
	}

	public static ClassInfo create(Class<?> clazz, Map<Class<Annotation>, Annotation> annotations) {
		// the @Serialize annotation counts
		ClassInfo classInfo = new ClassInfo(clazz, annotations);
		if (dedupMap.containsKey(classInfo)) return dedupMap.get(classInfo);
		dedupMap.put(classInfo, classInfo);
		return classInfo;
	}

	private List<ClassSerializerMetadata.FieldEntry> getFields(ScanHandler factory, Predicate<? super Field> filter) {
		List<ClassSerializerMetadata.FieldEntry> info = new ArrayList<>();
		for (Field declaredField : clazz.getDeclaredFields()) {
			if (filter.test(declaredField)) {
				Type genericType = declaredField.getGenericType();
				TypeInfo classInfo;

				try {
					classInfo = factory.create(this, declaredField.getType(), genericType, declaredField.getAnnotatedType());
				} catch (HyphenException hyphenException) {
					throw hyphenException.setName(declaredField.getName());
				}

				if (classInfo == ScanHandler.UNKNOWN_INFO)
					throw ThrowHandler.typeFail("Type could not be identified", this, declaredField);

				info.add(new ClassSerializerMetadata.FieldEntry(classInfo, declaredField.getModifiers(), declaredField.getName()));
			}
		}
		return info;
	}

	public List<ClassSerializerMetadata.FieldEntry> getAllFields(ScanHandler factory, Predicate<Field> filter) {
		List<ClassSerializerMetadata.FieldEntry> out = new ArrayList<>();
		Class<?> superclass = this.clazz.getSuperclass();
		if (superclass != null) {
			TypeInfo typeInfo = factory.create(this, superclass, clazz.getGenericSuperclass(), clazz.getAnnotatedSuperclass());
			if (typeInfo instanceof ClassInfo classInfo) {
				out.addAll(classInfo.getAllFields(factory, filter));
			}
		}
		out.addAll(getFields(factory, filter));
		return out;
	}

	@Override
	public SerializerMetadata createMetadata(ScanHandler factory) {
		if (this.metadata != null) return this.metadata;

		var methods = factory.methods;
		var implementations = factory.implementations;

		var methodMetadata = new ClassSerializerMetadata(this);
		methods.put(this, methodMetadata);

		if (implementations.containsKey(this.clazz)) {
			methodMetadata.fields.put(null, implementations.get(this.clazz).apply(this));
			return methodMetadata;
		}

		//get the fields
		var allFields = this.getAllFields(factory, field -> field.getDeclaredAnnotation(Serialize.class) != null);
		//check if it exists / if its accessible
		ScanUtils.checkConstructor(factory, this);
		for (ClassSerializerMetadata.FieldEntry fieldInfo : allFields) {
			try {
				methodMetadata.fields.put(fieldInfo, factory.getDefinition(fieldInfo, this));
			} catch (HyphenException hyphenException) {
				throw hyphenException.addParent(this, fieldInfo.name());
			}
		}

		return this.metadata = methodMetadata;
	}

	@Override
	public String toFancyString() {
		return this.clazz.getSimpleName();
	}

	@Override
	public String getMethodName(boolean absolute) {
		return absolute ? clazz.getName() : clazz.getSimpleName();
	}

	@Override
	public String toString() {
		return this.clazz.getSimpleName();
	}

}
