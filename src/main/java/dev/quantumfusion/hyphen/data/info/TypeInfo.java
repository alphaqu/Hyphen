package dev.quantumfusion.hyphen.data.info;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.annotation.SerComplexSubClass;
import dev.quantumfusion.hyphen.annotation.SerComplexSubClasses;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.data.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.options.AnnotationParser;
import dev.quantumfusion.hyphen.thr.ClassScanException;
import dev.quantumfusion.hyphen.thr.NotYetImplementedException;
import dev.quantumfusion.hyphen.thr.ThrowEntry;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.util.ScanUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import static dev.quantumfusion.hyphen.ScanHandler.UNKNOWN_INFO;

public abstract class TypeInfo {
	public final Class<?> clazz;
	public final Map<Class<Annotation>, Annotation> annotations;

	public TypeInfo(Class<?> clazz, Map<Class<Annotation>, Annotation> annotations) {
		this.clazz = clazz;
		this.annotations = annotations;
	}

	public static TypeInfo create(TypeInfo source, Class<?> fieldType, Type genericType, @Nullable AnnotatedType annotatedType) {
		if (source == null) {
			throw ThrowHandler.fatal(NullPointerException::new, "source is null",
					ThrowEntry.of("ClassType", fieldType),
					ThrowEntry.of("Type", genericType),
					ThrowEntry.of("AnnotatedType", annotatedType)
			);
		}

		var options = AnnotationParser.parseAnnotations(annotatedType);

		// check if field is polymorphic
		if (options.containsKey(SerSubclasses.class) || options.containsKey(SerComplexSubClass.class) || options.containsKey(SerComplexSubClasses.class)) {
			return PolymorphicTypeInfo.create(source, fieldType, genericType, options, annotatedType);
		}


		//Object / int / Object[] / int[]
		if (genericType instanceof Class clazz) {
			if (clazz.isArray()) {
				Class componentType = clazz.getComponentType();
				return ArrayInfo.create(source, clazz, options, create(source, componentType, componentType, null));
			} else {
				return ClassInfo.create(clazz, options);
			}
		}


		//Thing<T,T>
		if (genericType instanceof ParameterizedType type) {
			if (annotatedType instanceof AnnotatedParameterizedType parameterizedType) {
				return ParameterizedClassInfo.create(options, source, type, parameterizedType);
			} else if (annotatedType == null) {
				return ParameterizedClassInfo.create(options, source, type, null);
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
					return TypeClassInfo.create(source, classInfo.clazz, classInfo.annotations, typeName, ScanUtils.getClazz(typeVariable.getBounds()[0]), classInfo);
				}
			}

			/*throw ThrowHandler.typeFail("Type could not be identified", source, fieldType, typeVariable);*/
			return UNKNOWN_INFO;
		}

		//T[] arrrrrrrr
		if (genericType instanceof GenericArrayType genericArrayType) {
			//get component class
			if (annotatedType instanceof AnnotatedArrayType annotatedArrayType) {
				var componentType = genericArrayType.getGenericComponentType();
				var classInfo = create(source, fieldType, componentType, annotatedArrayType.getAnnotatedGenericComponentType());
				return ArrayInfo.create(source, fieldType, options, classInfo);
			}
			throw new RuntimeException();
		}

		if (genericType instanceof WildcardType wildcardType){
			if(wildcardType.getLowerBounds().length == 0 && wildcardType.getUpperBounds().length == 1 && wildcardType.getUpperBounds()[0] == Object.class){
				// just a <?>
				return UNKNOWN_INFO;
			}


			throw ThrowHandler.fatal(NotYetImplementedException::new, "Can't handle wildcards yet",
					ThrowEntry.of("GenericType", wildcardType),
					ThrowEntry.of("UpperBounds", Arrays.toString(wildcardType.getUpperBounds())),
					ThrowEntry.of("LowerBounds", Arrays.toString(wildcardType.getLowerBounds()))
					);
		}

		throw ThrowHandler.fatal(IllegalArgumentException::new, "Unknown generic type",
				ThrowEntry.of("GenericType", genericType)
		);
	}

	public static TypeInfo createFromPolymorphicType(TypeInfo source, Class<?> fieldClass, Class<?> subType, Type fieldType, AnnotatedType annotatedFieldType) {
		TypeVariable<? extends Class<?>>[] typeParameters = subType.getTypeParameters();

		if (typeParameters.length != 0) {
			// let's try to figure out the types
			if (fieldType instanceof ParameterizedType parameterizedFieldType) {
				LinkedHashMap<String, TypeInfo> types = ScanUtils.findTypes(source, fieldClass, subType, parameterizedFieldType, (AnnotatedParameterizedType) annotatedFieldType);

				if (types == null) {
					throw ThrowHandler.fatal(
							ClassScanException::new, "Failed to find the type",
							ThrowEntry.of("SourceClass", source),
							ThrowEntry.of("SubType", subType),
							ThrowEntry.of("FieldClass", fieldClass),
							ThrowEntry.of("ParameterizedFieldType", parameterizedFieldType)
					);
				}


				return new ParameterizedClassInfo(subType, Map.of(), types);
			} else {
				throw ThrowHandler.fatal(ClassScanException::new, "*Confused noizes*",
						ThrowEntry.of("SourceClass", source),
						ThrowEntry.of("SubType", subType),
						ThrowEntry.of("Poly", fieldClass));
			}
		}

		//Object / int / Object[] / int[]
		if (true /* genericType instanceof Class clazz */) {
			return new ClassInfo(subType, new HashMap<>());
		}


		//Thing<T,T>
		if (fieldType instanceof ParameterizedType type) {
			// TODO: think
			/*
			if (annotatedType instanceof AnnotatedParameterizedType parameterizedType) {
				LinkedHashMap<String, TypeInfo> out = mapTypes(source, type, parameterizedType);
				return new ParameterizedClassInfo((Class<?>) type.getRawType(), options, this, out);
			}*/
			throw new RuntimeException();
		}

		//T thing
		if (fieldType instanceof TypeVariable typeVariable) {
			LinkedHashMap<String, TypeInfo> typeMap;
			if (source instanceof ParameterizedClassInfo info) {
				typeMap = info.types;
			} else typeMap = new LinkedHashMap<>();
			var classInfo = typeMap.get(typeVariable.getName());

			if (classInfo == null) {
				throw ThrowHandler.typeFail("Type could not be identified", source, fieldClass, typeVariable);
			}
			//safety first!
			return classInfo.copy();
		}

		//T[] arrrrrrrr
		if (fieldType instanceof GenericArrayType genericArrayType) {
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

	public abstract SerializerMetadata createMetadata(ScanHandler factory);

	public abstract String toFancyString();

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


	private record DedupKey(TypeInfo source, Class<?> fieldType, Type genericType, @Nullable AnnotatedType annotatedType) {
	}

}
