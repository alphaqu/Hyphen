package dev.quantumfusion.hyphen.data.info;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.annotation.SerComplexSubClass;
import dev.quantumfusion.hyphen.annotation.SerComplexSubClasses;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.data.metadata.SubclassSerializerMetadata;
import dev.quantumfusion.hyphen.data.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.thr.ThrowEntry;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.util.Color;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.*;

public class SubclassInfo extends TypeInfo {
	public final List<? extends TypeInfo> classInfos;

	public SubclassInfo(Class<?> clazz, Map<Class<Annotation>, Annotation> annotations, List<? extends TypeInfo> classInfos) {
		super(clazz, annotations);
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
		Class<?>[] value = subclasses.value();
		List<TypeInfo> subInfos = new ArrayList<>(value.length);

		for (Class<?> subclass : value) {
			var subClassInfo = ScanHandler.createFromPolymorphicType(source, fieldType, subclass, genericType, annotatedType);
			subInfos.add(subClassInfo);
		}

		return new SubclassInfo(fieldType, options, subInfos);
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

	@Override
	public SubclassInfo copy() {
		return new SubclassInfo(this.clazz, new HashMap<>(this.annotations), new ArrayList<>(this.classInfos));
	}
}
