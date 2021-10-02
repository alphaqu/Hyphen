package dev.quantumfusion.hyphen.info;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.annotation.SerComplexSubClass;
import dev.quantumfusion.hyphen.annotation.SerComplexSubClasses;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.codegen.method.SubclassMethod;
import dev.quantumfusion.hyphen.thr.ThrowEntry;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.ClassScanException;
import dev.quantumfusion.hyphen.util.Color;
import dev.quantumfusion.hyphen.util.TypeUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class SubclassInfo extends TypeInfo {
	public final List<? extends TypeInfo> classInfos;
	private final TypeInfo field;

	public SubclassInfo(Class<?> clazz, Type type, AnnotatedType annotatedType, Map<Class<? extends Annotation>, Annotation> annotations, TypeInfo field, List<? extends TypeInfo> classInfos) {
		super(clazz, type, annotatedType, annotations);
		this.field = field;
		this.classInfos = classInfos;
	}

	public static boolean check(Map<Class<? extends Annotation>, Annotation> annotations) {
		return annotations.containsKey(SerSubclasses.class) || annotations.containsKey(SerComplexSubClass.class) || annotations.containsKey(SerComplexSubClasses.class);
	}

	public static SubclassInfo create(ScanHandler handler, TypeInfo source, Class<?> superClass, Type genericType, AnnotatedType annotatedType, Map<Class<? extends Annotation>, Annotation> options) {
		var globalSubclasses = handler.subclasses;
		if (options.containsKey(SerComplexSubClass.class) || options.containsKey(SerComplexSubClasses.class)) {
			throw ThrowHandler.fatal(
					IllegalStateException::new, "NYI: handling of SerComplexSubClass annotation",
					ThrowEntry.of("Source", source),
					ThrowEntry.of("ClassType", superClass),
					ThrowEntry.of("Annotations", options));
		}

		SerSubclasses info = (SerSubclasses) options.get(SerSubclasses.class);
		var value = info.value();
		var dedup = new LinkedHashSet<Class<?>>();


		if (info.key() != null) {
			var classes = globalSubclasses.get(info.key());
			if (classes != null) dedup.addAll(classes);
		}

		if (!info.override()) {
			var classes = globalSubclasses.get(superClass);
			if (classes != null) dedup.addAll(classes);
		}

		if (value != null) dedup.addAll(List.of(value));

		var out = new ArrayList<TypeInfo>(dedup.size());
		for (Class<?> subClass : dedup) {
			TypeInfo subClassInfo;
			var typeParameters = subClass.getTypeParameters();

			if (typeParameters.length != 0) {
				if (genericType instanceof ParameterizedType parameterizedType) {
					AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) annotatedType;
					var types = TypeUtil.findTypes(handler, source, superClass, parameterizedType, annotatedParameterizedType, subClass);

					if (types == null) {
						throw ThrowHandler.fatal(
								ClassScanException::new, "Failed to find the type",
								ThrowEntry.of("SourceClass", source),
								ThrowEntry.of("SubType", subClass),
								ThrowEntry.of("FieldClass", superClass),
								ThrowEntry.of("ParameterizedFieldType", parameterizedType)
						);
					}

					subClassInfo = new ParameterizedInfo(subClass, parameterizedType, annotatedParameterizedType, Map.of(), types);
				} else {
					throw ThrowHandler.fatal(ClassScanException::new, "*Confused noizes*",
							ThrowEntry.of("SourceClass", source),
							ThrowEntry.of("SubType", subClass),
							ThrowEntry.of("Poly", superClass));
				}
			} else
				subClassInfo = handler.create(source, subClass, null, null);
			out.add(subClassInfo);
		}

		return new SubclassInfo(superClass, genericType, annotatedType, options, handler.create(source, superClass, genericType, null), out);
	}

	public MethodMetadata createMetadata(ScanHandler handler) {
		return SubclassMethod.create(this, handler);
	}

	@Override
	public String toFancyString() {
		StringJoiner parameterJoiner = new StringJoiner(
				Color.WHITE + ", ",
				Color.GREEN + "Subclass" + Color.GREEN + "[",
				Color.GREEN + "]");
		parameterJoiner.setEmptyValue("");
		for (TypeInfo t : this.classInfos) {
			parameterJoiner.add(t.toFancyString());
		}
		return parameterJoiner.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SubclassInfo that)) return false;
		return Objects.equals(this.classInfos, that.classInfos);
	}

	@Override
	public int hashCode() {
		return Objects.hash(classInfos);
	}

	@Override
	public String getMethodName(boolean absolute) {
		StringBuilder builder = new StringBuilder();
		builder.append("Sub_E_");
		for (TypeInfo classInfo : classInfos) {
			builder.append(classInfo.getMethodName(absolute));
		}
		return builder.append("_3_").toString();
	}

	@Override
	public Class<?> getClazz() {
		return field.getClazz();
	}

	@Override
	public String toString() {
		StringJoiner parameterJoiner = new StringJoiner(
				", ",
				"Subclass[",
				"]");
		parameterJoiner.setEmptyValue("");
		for (TypeInfo t : this.classInfos) {
			parameterJoiner.add(t.toString());
		}
		return parameterJoiner.toString();
	}
}
