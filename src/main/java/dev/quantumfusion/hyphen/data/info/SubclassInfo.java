package dev.quantumfusion.hyphen.data.info;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.annotation.SerComplexSubClass;
import dev.quantumfusion.hyphen.annotation.SerComplexSubClasses;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.data.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.data.metadata.SubclassSerializerMetadata;
import dev.quantumfusion.hyphen.thr.ClassScanException;
import dev.quantumfusion.hyphen.thr.ThrowEntry;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.util.Color;
import dev.quantumfusion.hyphen.util.ScanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class SubclassInfo extends TypeInfo {
	public final TypeInfo field;
	public final List<? extends TypeInfo> classInfos;

	public SubclassInfo(Class<?> clazz, Map<Class<Annotation>, Annotation> annotations, TypeInfo field, List<? extends TypeInfo> classInfos) {
		super(clazz, annotations);
		this.field = field;
		this.classInfos = classInfos;
	}

	public static SubclassInfo create(TypeInfo source, Class<?> fieldType, Type genericType, Map<Class<Annotation>, Annotation> options, AnnotatedType annotatedType) {
		if (options.containsKey(SerComplexSubClass.class) || options.containsKey(SerComplexSubClasses.class)) {
			throw ThrowHandler.fatal(
					IllegalStateException::new, "NYI: handling of SerComplexSubClass annotation",
					ThrowEntry.of("Source", source),
					ThrowEntry.of("ClassType", fieldType),
					ThrowEntry.of("Annotations", options));
		}

		SerSubclasses subclasses = (SerSubclasses) options.get(SerSubclasses.class);
		var value = subclasses.value();
		var subInfos = new ArrayList<TypeInfo>(value.length);

		for (Class<?> subclass : value) {
			TypeInfo subClassInfo;
			var typeParameters = subclass.getTypeParameters();

			if (typeParameters.length != 0) {
				if (genericType instanceof ParameterizedType parameterizedFieldType) {
					var types = ScanUtils.findTypes(source, fieldType, subclass, parameterizedFieldType, (AnnotatedParameterizedType) annotatedType);

					if (types == null) {
						throw ThrowHandler.fatal(
								ClassScanException::new, "Failed to find the type",
								ThrowEntry.of("SourceClass", source),
								ThrowEntry.of("SubType", subclass),
								ThrowEntry.of("FieldClass", fieldType),
								ThrowEntry.of("ParameterizedFieldType", parameterizedFieldType)
						);
					}

					subClassInfo = new ParameterizedClassInfo(subclass, Map.of(), types);
				} else {
					throw ThrowHandler.fatal(ClassScanException::new, "*Confused noizes*",
							ThrowEntry.of("SourceClass", source),
							ThrowEntry.of("SubType", subclass),
							ThrowEntry.of("Poly", fieldType));
				}
			} else subClassInfo = ClassInfo.create(subclass, Map.of());


			subInfos.add(subClassInfo);
		}

		return new SubclassInfo(fieldType, options, ScanHandler.create(source, fieldType, genericType, null), subInfos);
	}


	public SerializerMetadata createMetadata(ScanHandler factory) {
		var subTypeMap = new LinkedHashMap<Class<?>, TypeInfo>();
		var methodMetadata = new SubclassSerializerMetadata(this, subTypeMap);

		for (TypeInfo subTypeInfo : this.classInfos) {
			if (subTypeMap.containsKey(subTypeInfo.clazz)) {
				// TODO: throw error, cause there is a duplicated class
				//		 or should this be done earlier
			}

			factory.createSerializeMetadata(subTypeInfo);
			subTypeMap.put(subTypeInfo.clazz, subTypeInfo);
		}
		return methodMetadata;
	}

	@Override
	public String toFancyString() {
		StringJoiner parameterJoiner = new StringJoiner(
				Color.WHITE + ", ",
				Color.CYAN + "Poly" + Color.PURPLE + "[",
				Color.PURPLE + "]");
		parameterJoiner.setEmptyValue("");
		for (TypeInfo t : this.classInfos) {
			parameterJoiner.add(t.toFancyString());
		}
		return parameterJoiner.toString();
	}

	@Override
	public String getMethodName(boolean absolute) {
		StringBuilder builder = new StringBuilder();
		builder.append("Subclass");
		for (TypeInfo classInfo : classInfos) {
			builder.append(classInfo.getMethodName(absolute));
		}
		return builder.toString();
	}

	@Override
	public Class<?> getClazz() {
		return field.getClazz();
	}

	@Override
	public String toString() {
		StringJoiner parameterJoiner = new StringJoiner(
				", ",
				"Poly[",
				"]");
		parameterJoiner.setEmptyValue("");
		for (TypeInfo t : this.classInfos) {
			parameterJoiner.add(t.toString());
		}
		return parameterJoiner.toString();
	}
}
