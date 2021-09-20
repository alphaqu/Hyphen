package dev.quantumfusion.hyphen.data.info;

import dev.quantumfusion.hyphen.ObjectSerializationDef;
import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.data.metadata.ClassSerializerMetadata;
import dev.quantumfusion.hyphen.data.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.thr.HyphenException;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.util.Color;
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


	private ClassInfo[] getSuperClasses(ClassInfo in, int depth) {
		Class<?> clazz = in.clazz;
		Class<?> superclass = clazz.getSuperclass();

		if (superclass == null)
			return new ClassInfo[depth];


		TypeInfo typeInfo = ScanHandler.create(in, superclass, clazz.getGenericSuperclass(), clazz.getAnnotatedSuperclass());

		if (!(typeInfo instanceof ClassInfo info)) {
			// this should always return a class info, unless you put a `SubClasses` annotations on an extends clause
			throw new IllegalStateException("I think you put `@SubClasses` on a extends clause?");
		}

		ClassInfo[] out = getSuperClasses(info, depth + 1);
		out[depth] = info;
		return out;
	}

	private List<ClassSerializerMetadata.FieldEntry> getFields(Predicate<? super Field> filter) {
		List<ClassSerializerMetadata.FieldEntry> info = new ArrayList<>();
		for (Field declaredField : clazz.getDeclaredFields()) {
			if (filter.test(declaredField)) {
				Type genericType = declaredField.getGenericType();
				TypeInfo classInfo;

				try {
					classInfo = ScanHandler.create(this, declaredField.getType(), genericType, declaredField.getAnnotatedType());
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

	private List<ClassSerializerMetadata.FieldEntry> getAllFields(Predicate<Field> filter) {
		List<ClassSerializerMetadata.FieldEntry> out = new ArrayList<>();
		for (ClassInfo superClass : getSuperClasses(this, 0)) {
			out.addAll(superClass.getFields(filter));
		}
		out.addAll(getFields(filter));
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
		var allFields = this.getAllFields(field -> field.getDeclaredAnnotation(Serialize.class) != null);
		//check if it exists / if its accessible
		ScanUtils.checkConstructor(allFields, this);
		for (ClassSerializerMetadata.FieldEntry fieldInfo : allFields) {
			try {
				ObjectSerializationDef def = factory.getDefinition(fieldInfo, this);
				methodMetadata.fields.put(fieldInfo, def);
			} catch (HyphenException hyphenException) {
				throw hyphenException.addParent(this, fieldInfo.name());
			}
		}

		return this.metadata = methodMetadata;
	}


	@Override
	public String toFancyString() {
		return Color.YELLOW + this.clazz.getSimpleName();
	}

	@Override
	public String getMethodName(boolean absolute) {
		return absolute ? clazz.getName() : clazz.getSimpleName();
	}

	@Override
	public String toString() {
		return this.clazz.getSimpleName();
	}

	@Override
	public ClassInfo copy() {
		return new ClassInfo(this.clazz, new HashMap<>(this.annotations));
	}
}
