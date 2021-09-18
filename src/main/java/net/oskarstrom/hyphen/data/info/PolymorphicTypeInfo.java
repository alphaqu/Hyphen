package net.oskarstrom.hyphen.data.info;

import net.oskarstrom.hyphen.ScanHandler;
import net.oskarstrom.hyphen.annotation.SerComplexSubClass;
import net.oskarstrom.hyphen.annotation.SerComplexSubClasses;
import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.data.metadata.JunctionSerializerMetadata;
import net.oskarstrom.hyphen.data.metadata.SerializerMetadata;
import net.oskarstrom.hyphen.thr.ThrowHandler;
import net.oskarstrom.hyphen.util.Color;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.*;

public class PolymorphicTypeInfo extends TypeInfo {
	public final List<? extends TypeInfo> classInfos;

	public PolymorphicTypeInfo(Class<?> clazz, Map<Class<Annotation>, Annotation> annotations, List<? extends TypeInfo> classInfos) {
		super(clazz, annotations);
		this.classInfos = classInfos;
	}

	public static PolymorphicTypeInfo create(TypeInfo source, Class<?> fieldType, Type genericType, Map<Class<Annotation>, Annotation>  options, AnnotatedType annotatedType) {
		if (options.containsKey(SerComplexSubClass.class) || options.containsKey(SerComplexSubClasses.class)) {
			throw ThrowHandler.fatal(
					IllegalStateException::new, "NYI: handling of SerComplexSubClass annotation",
					ThrowHandler.ThrowEntry.of("Source", source),
					ThrowHandler.ThrowEntry.of("ClassType", fieldType),
					ThrowHandler.ThrowEntry.of("Annotations", options));
		}

		SerSubclasses subclasses = (SerSubclasses) options.get(SerSubclasses.class);
		Class<?>[] value = subclasses.value();
		List<TypeInfo> subInfos = new ArrayList<>(value.length);

		for (Class<?> subclass : value) {
			var subClassInfo = TypeInfo.createFromPolymorphicType(source, fieldType, subclass, genericType, annotatedType);
			subInfos.add(subClassInfo);
		}

		return new PolymorphicTypeInfo(fieldType, options, subInfos);
	}


	public SerializerMetadata createMetadata(ScanHandler factory) {
		var methodMetadata = new JunctionSerializerMetadata(this);
		var subTypeMap = methodMetadata.subtypes;

		for (TypeInfo subTypeInfo : this.classInfos) {
			if (subTypeMap.containsKey(subTypeInfo.clazz)) {
				// TODO: throw error, cause there is a duplicated class
				//		 or should this be done earlier
			}

			subTypeMap.put(subTypeInfo.clazz, factory.createSerializeMetadata(subTypeInfo));
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
	public PolymorphicTypeInfo copy() {
		return new PolymorphicTypeInfo(this.clazz, new HashMap<>(this.annotations), new ArrayList<>(this.classInfos));
	}
}
