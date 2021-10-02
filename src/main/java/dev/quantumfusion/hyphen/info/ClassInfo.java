package dev.quantumfusion.hyphen.info;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.codegen.FieldEntry;
import dev.quantumfusion.hyphen.codegen.method.ClassMethod;
import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.HyphenException;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassInfo extends TypeInfo implements Type {
	private MethodMetadata metadata;

	public ClassInfo(Class<?> clazz, Type type, @Nullable AnnotatedType annotatedType, Map<Class<? extends Annotation>, Annotation> annotations) {
		super(clazz, type, annotatedType, annotations);
	}

	public ClassInfo(Class<?> clazz, Map<Class<? extends Annotation>, Annotation> annotations) {
		super(clazz, clazz, null, annotations);
	}

	public static TypeInfo createType(ScanHandler handler, TypeInfo source, Class<?> type, @Nullable AnnotatedType annotatedType, Map<Class<? extends Annotation>, Annotation> annotations) {
		return new ClassInfo(type, type, annotatedType, annotations);
	}

	private List<FieldEntry> getFields(ScanHandler factory) {
		List<FieldEntry> info = new ArrayList<>();
		for (Field field : this.clazz.getDeclaredFields()) {
			if (classAnnotations.get(Serialize.class) != null || field.getDeclaredAnnotation(Serialize.class) != null) {
				try {
					Type genericType = field.getGenericType();

					TypeInfo classInfo = factory.create(this, field.getType(), genericType, field.getAnnotatedType());

					if (classInfo == ScanHandler.UNKNOWN_INFO)
						throw ThrowHandler.typeFail("Type could not be identified", this, field);

					info.add(new FieldEntry(classInfo, field.getModifiers(), field.getName()));
				} catch (HyphenException hyphenException) {
					throw hyphenException.addParent(this, field.getName());
				}
			}
		}

		return info;
	}

	public List<FieldEntry> getAllFields(ScanHandler factory) {
		List<FieldEntry> out = new ArrayList<>();
		Class<?> superclass = this.clazz.getSuperclass();
		if (superclass != null) {
			try {
				TypeInfo typeInfo = factory.create(this, superclass, this.clazz.getGenericSuperclass(), this.clazz.getAnnotatedSuperclass());
				if (typeInfo instanceof ClassInfo classInfo) {
					out.addAll(classInfo.getAllFields(factory));
				}
			} catch (HyphenException hyphenException) {
				throw hyphenException.addParent(this, "superclass");
			}
		}
		out.addAll(this.getFields(factory));
		return out;
	}

	@Override
	public MethodMetadata createMetadata(ScanHandler handler) {
		if (this.metadata != null) return this.metadata;
		return this.metadata = ClassMethod.create(this, handler);
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
