package dev.quantumfusion.hyphen.info;

import dev.quantumfusion.hyphen.ScanHandler;
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
import java.util.function.Predicate;

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

	private List<FieldEntry> getFields(ScanHandler factory, Predicate<? super Field> filter) {
		List<FieldEntry> info = new ArrayList<>();
		for (Field declaredField : this.clazz.getDeclaredFields()) {
			if (filter.test(declaredField)) {
				try {
					Type genericType = declaredField.getGenericType();

					TypeInfo classInfo = factory.create(this, declaredField.getType(), genericType, declaredField.getAnnotatedType());

					if (classInfo == ScanHandler.UNKNOWN_INFO)
						throw ThrowHandler.typeFail("Type could not be identified", this, declaredField);

					info.add(new FieldEntry(classInfo, declaredField.getModifiers(), declaredField.getName()));
				} catch (HyphenException hyphenException) {
					throw hyphenException.addParent(this, declaredField.getName());
				}
			}
		}

		return info;
	}

	public List<FieldEntry> getAllFields(ScanHandler factory, Predicate<? super Field> filter) {
		List<FieldEntry> out = new ArrayList<>();
		Class<?> superclass = this.clazz.getSuperclass();
		if (superclass != null) {
			try {
				TypeInfo typeInfo = factory.create(this, superclass, this.clazz.getGenericSuperclass(), this.clazz.getAnnotatedSuperclass());
				if (typeInfo instanceof ClassInfo classInfo) {
					out.addAll(classInfo.getAllFields(factory, filter));
				}
			} catch (HyphenException hyphenException) {
				throw hyphenException.addParent(this, "superclass");
			}
		}
		out.addAll(this.getFields(factory, filter));
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
