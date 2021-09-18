package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.SerializerFactory;
import net.oskarstrom.hyphen.annotation.SerComplexSubClass;
import net.oskarstrom.hyphen.annotation.SerComplexSubClasses;
import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.thr.ThrowHandler;
import net.oskarstrom.hyphen.util.Color;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.*;

public class PolymorphicTypeInfo extends TypeInfo {
	public final List<? extends TypeInfo> classInfos;

	public PolymorphicTypeInfo(Class<?> clazz, Map<Class<Annotation>, Object> annotations, List<? extends TypeInfo> classInfos, SerializerFactory factory) {
		super(clazz, annotations, factory);
		this.classInfos = classInfos;
	}

	public static PolymorphicTypeInfo create(SerializerFactory factory, ClassInfo source, Class<?> classType, Type genericType, Map<Class<Annotation>, Object> options, AnnotatedType annotatedType) {
		if (options.containsKey(SerComplexSubClass.class) || options.containsKey(SerComplexSubClasses.class)) {
			throw ThrowHandler.fatal(
					IllegalStateException::new, "NYI: handling of SerComplexSubClass annotation",
					ThrowHandler.ThrowEntry.of("Source", source),
					ThrowHandler.ThrowEntry.of("ClassType", classType),
					ThrowHandler.ThrowEntry.of("Annotations", options));
		}

		Class<?>[] subclasses = (Class<?>[]) options.get(SerSubclasses.class);
		List<TypeInfo> subInfos = new ArrayList<>(subclasses.length);

		for (Class<?> subclass : subclasses) {
			var subClassInfo = TypeInfo.createFromPolymorphicType(factory, source, classType, genericType, annotatedType, subclass);
			subInfos.add(subClassInfo);
		}

		return new PolymorphicTypeInfo(classType, options, subInfos, factory);
	}


	public JunctionSerializerMetadata createMeta() {
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
		return new PolymorphicTypeInfo(this.clazz, new HashMap<>(this.annotations), new ArrayList<>(this.classInfos), this.factory);
	}
}
