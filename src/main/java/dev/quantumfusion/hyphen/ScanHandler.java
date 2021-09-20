package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.annotation.HyphenOptionAnnotation;
import dev.quantumfusion.hyphen.annotation.SerComplexSubClass;
import dev.quantumfusion.hyphen.annotation.SerComplexSubClasses;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.data.info.*;
import dev.quantumfusion.hyphen.data.metadata.ClassSerializerMetadata;
import dev.quantumfusion.hyphen.data.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.gen.impl.MethodCallDef;
import dev.quantumfusion.hyphen.thr.ClassScanException;
import dev.quantumfusion.hyphen.thr.NotYetImplementedException;
import dev.quantumfusion.hyphen.thr.ThrowEntry;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.util.Color;
import dev.quantumfusion.hyphen.util.ScanUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class ScanHandler {
	public static final TypeInfo UNKNOWN_INFO = new ClassInfo(null, null) {
		@Override
		public String toString() {
			return "UNKNOWN";
		}

		@Override
		public String toFancyString() {
			return Color.BLUE + "?";
		}
	};
	public final Map<TypeInfo, SerializerMetadata> methods;
	public final Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> implementations;
	@Nullable
	private final DebugHandler debugHandler;

	protected ScanHandler(Map<TypeInfo, SerializerMetadata> methods, Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> implementations, boolean debug) {
		this.implementations = implementations;
		this.debugHandler = debug ? new DebugHandler(this) : null;
		this.methods = methods;
	}

	public static TypeInfo create(TypeInfo source, Class<?> fieldType, Type genericType, @Nullable AnnotatedType annotatedType) {
		if (source == null) {
			throw ThrowHandler.fatal(NullPointerException::new, "source is null",
					ThrowEntry.of("ClassType", fieldType),
					ThrowEntry.of("Type", genericType),
					ThrowEntry.of("AnnotatedType", annotatedType)
			);
		}

		Map<Class<Annotation>, Annotation> options = new HashMap<>();
		if (annotatedType != null) {
			for (Annotation declaredAnnotation : annotatedType.getDeclaredAnnotations()) {
				if (declaredAnnotation.annotationType().getDeclaredAnnotation(HyphenOptionAnnotation.class) != null) {
					//noinspection unchecked
					options.put((Class<Annotation>) declaredAnnotation.annotationType(), declaredAnnotation);
				}
			}
		}

		// check if field is polymorphic
		if (options.containsKey(SerSubclasses.class) || options.containsKey(SerComplexSubClass.class) || options.containsKey(SerComplexSubClasses.class)) {
			return SubclassInfo.create(source, fieldType, genericType, options, annotatedType);
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

		if (genericType instanceof WildcardType wildcardType) {
			if (wildcardType.getLowerBounds().length == 0 && wildcardType.getUpperBounds().length == 1 && wildcardType.getUpperBounds()[0] == Object.class) {
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

		return ClassInfo.create(subType, Map.of());
	}

	public void scan(Class<?> clazz) {
		createSerializeMetadata(ClassInfo.create(clazz, Map.of()));

		if (debugHandler != null) {
			debugHandler.printMethods(methods);
		}
	}

	private SerializerMetadata createSerializeMetadataInternal(TypeInfo typeInfo) {
		return typeInfo.createMetadata(this);
	}

	public SerializerMetadata createSerializeMetadata(TypeInfo typeInfo) {
		if (this.methods.containsKey(typeInfo)) {
			return this.methods.get(typeInfo);
		}

		SerializerMetadata serializerMetadata = createSerializeMetadataInternal(typeInfo);
		this.methods.put(typeInfo, serializerMetadata);
		return serializerMetadata;
	}

	public ObjectSerializationDef getDefinition(ClassSerializerMetadata.FieldEntry field, ClassInfo source) {
		var classInfo = field.clazz();
		if (!(classInfo instanceof SubclassInfo) && implementations.containsKey(classInfo.clazz)) {
			return implementations.get(classInfo.clazz).apply(classInfo);
		} else {
			//check if field is legal
			//we don't do this on the serializerDef because they might do some grandpa 360 no-scopes on fields and access them another way
			ScanUtils.checkAccess(field.modifier(), () -> ThrowHandler.fieldAccessFail(field, source));

			this.createSerializeMetadata(classInfo);
			return new MethodCallDef(classInfo);
		}
	}

}
