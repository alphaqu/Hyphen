package net.oskarstrom.hyphen.data.info;

import net.oskarstrom.hyphen.SerializerFactory;
import net.oskarstrom.hyphen.annotation.SerComplexSubClass;
import net.oskarstrom.hyphen.annotation.SerComplexSubClasses;
import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.data.metadata.SerializerMetadata;
import net.oskarstrom.hyphen.options.AnnotationParser;
import net.oskarstrom.hyphen.thr.ClassScanException;
import net.oskarstrom.hyphen.thr.ThrowHandler;
import net.oskarstrom.hyphen.util.ScanUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public abstract class TypeInfo {
	public final Class<?> clazz;
	public final Map<Class<Annotation>, Object> annotations;

	TypeInfo(Class<?> clazz, Map<Class<Annotation>, Object> annotations) {
		this.clazz = clazz;
		this.annotations = annotations;
	}

	public static TypeInfo create(SerializerFactory factory, ClassInfo source, Class<?> classType, Type genericType, @Nullable AnnotatedType annotatedType) {
		if (source == null) {
			throw ThrowHandler.fatal(NullPointerException::new, "source is null",
					ThrowHandler.ThrowEntry.of("ClassType", classType),
					ThrowHandler.ThrowEntry.of("Type", genericType),
					ThrowHandler.ThrowEntry.of("AnnotatedType", annotatedType)
			);
		}

		var options = AnnotationParser.parseAnnotations(annotatedType, factory.hyphenAnnotations);

		// check if field is polymorphic
		if (options.containsKey(SerSubclasses.class) || options.containsKey(SerComplexSubClass.class) || options.containsKey(SerComplexSubClasses.class)) {
			return PolymorphicTypeInfo.create(factory, source, classType, genericType, options, annotatedType);
		}


		//Object / int / Object[] / int[]
		if (genericType instanceof Class clazz) {
			return new ClassInfo(clazz, options, factory);
		}


		//Thing<T,T>
		if (genericType instanceof ParameterizedType type) {
			if (annotatedType instanceof AnnotatedParameterizedType parameterizedType) {
				return ParameterizedClassInfo.create(factory, options, source, type, parameterizedType);
			}
			throw new RuntimeException();
		}

		//T thing
		if (genericType instanceof TypeVariable typeVariable) {
			if (source instanceof ParameterizedClassInfo info) {
				String typeName = typeVariable.getName();
				TypeInfo classInfo = info.types.get(typeName);
				if (classInfo != null) {
					// safety first!
					// kropp: why are we copying?
					return new TypeClassInfo(classInfo.clazz, classInfo.annotations, typeName, ScanUtils.castType(typeVariable.getBounds()[0]), classInfo);
				}
			}

			throw ThrowHandler.typeFail("Type could not be identified", source, classType, typeVariable);
		}

		//T[] arrrrrrrr
		if (genericType instanceof GenericArrayType genericArrayType) {
			//get component class
			if (annotatedType instanceof AnnotatedArrayType annotatedArrayType) {
				var componentType = genericArrayType.getGenericComponentType();
				var classInfo = create(factory, source, classType, componentType, annotatedArrayType.getAnnotatedGenericComponentType());
				if (classInfo == null) {
					throw ThrowHandler.typeFail("Array component could not be identified", source, classType, componentType);
				}
				return new ArrayInfo(classType, options, classInfo);
			}
			throw new RuntimeException();
		}

		return null;
	}

	public static TypeInfo createFromPolymorphicType(SerializerFactory factory, ClassInfo source, Class<?> poly, Type genericType, AnnotatedType annotatedGenericType, Class<?> subType) {
		TypeVariable<? extends Class<?>>[] typeParameters = subType.getTypeParameters();

		if (typeParameters.length != 0) {
			// let's try to figure out the types
			if (genericType instanceof ParameterizedType parameterizedType) {
				Map<String, AnnotatedType> types = ScanUtils.findTypes(subType, poly, parameterizedType, (AnnotatedParameterizedType) annotatedGenericType);

				if (types == null) {
					throw ThrowHandler.fatal(
							ClassScanException::new, "Failed to find the type",
							ThrowHandler.ThrowEntry.of("SourceClass", source),
							ThrowHandler.ThrowEntry.of("SubType", subType),
							ThrowHandler.ThrowEntry.of("Poly", poly),
							ThrowHandler.ThrowEntry.of("Type", parameterizedType)
					);
				}

				LinkedHashMap<String, TypeInfo> typeInfoMap = ScanUtils.mapAllTypes(factory, source, typeParameters, types);

				return new ParameterizedClassInfo(subType, Map.of(), factory, typeInfoMap);
			} else {
				throw ThrowHandler.fatal(ClassScanException::new, "*Confused noizes*",
						ThrowHandler.ThrowEntry.of("SourceClass", source),
						ThrowHandler.ThrowEntry.of("SubType", subType),
						ThrowHandler.ThrowEntry.of("Poly", poly));
			}
		}

		//Object / int / Object[] / int[]
		if (true /* genericType instanceof Class clazz */) {
			return new ClassInfo(subType, Map.of(), factory);
		}


		//Thing<T,T>
		if (genericType instanceof ParameterizedType type) {
			// TODO: think
			/*
			if (annotatedType instanceof AnnotatedParameterizedType parameterizedType) {
				LinkedHashMap<String, TypeInfo> out = mapTypes(source, type, parameterizedType);
				return new ParameterizedClassInfo((Class<?>) type.getRawType(), options, this, out);
			}*/
			throw new RuntimeException();
		}

		//T thing
		if (genericType instanceof TypeVariable typeVariable) {
			LinkedHashMap<String, TypeInfo> typeMap;
			if (source instanceof ParameterizedClassInfo info) {
				typeMap = info.types;
			} else typeMap = new LinkedHashMap<>();
			var classInfo = typeMap.get(typeVariable.getName());

			if (classInfo == null) {
				throw ThrowHandler.typeFail("Type could not be identified", source, poly, typeVariable);
			}
			//safety first!
			return classInfo.copy();
		}

		//T[] arrrrrrrr
		if (genericType instanceof GenericArrayType genericArrayType) {
			//get component class
			// TODO: think
			/*
			if (annotatedType instanceof AnnotatedArrayType annotatedArrayType) {
				var componentType = genericArrayType.getGenericComponentType();
				var classInfo = createClassInfo(source, classType, componentType, annotatedArrayType.getAnnotatedGenericComponentType());
				if (classInfo == null) {
					throw ThrowHandler.typeFail("Array component could not be identified", source, classType, componentType);
				}
				return new ArrayInfo(classType, options, classInfo);
			}*/
			throw new RuntimeException();
		}

		return null;
	}

	public abstract String toFancyString();

	public abstract SerializerMetadata createMeta(SerializerFactory factory);

	@Override
	public boolean equals(Object o) {
		return this == o
				|| o instanceof TypeInfo typeInfo
				&& Objects.equals(this.clazz, typeInfo.clazz)
				&& Objects.equals(this.annotations, typeInfo.annotations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.clazz, this.annotations);
	}

	public abstract TypeInfo copy();

	public Class<?> getRawClass() {
		return this.clazz;
	}
}
