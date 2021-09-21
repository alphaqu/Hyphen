package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.annotation.SerComplexSubClass;
import dev.quantumfusion.hyphen.annotation.SerComplexSubClasses;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.data.info.*;
import dev.quantumfusion.hyphen.data.metadata.ClassSerializerMetadata;
import dev.quantumfusion.hyphen.data.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.gen.impl.MethodCallDef;
import dev.quantumfusion.hyphen.thr.HyphenException;
import dev.quantumfusion.hyphen.thr.NotYetImplementedException;
import dev.quantumfusion.hyphen.thr.ThrowEntry;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.util.Color;
import dev.quantumfusion.hyphen.util.ScanUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.Arrays;
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

	public static TypeInfo create(TypeInfo source, Class<?> fieldType, @Nullable Type genericType, @Nullable AnnotatedType annotatedType) {
		if (genericType == null) genericType = fieldType;
		try {
			if (source == null) {
				throw ThrowHandler.fatal(NullPointerException::new, "Source is null",
						ThrowEntry.of("ClassType", fieldType),
						ThrowEntry.of("Type", genericType),
						ThrowEntry.of("AnnotatedType", annotatedType)
				);
			}

			var options = ScanUtils.parseAnnotations(annotatedType);

			// @Subclasses(SuperString.class, WaitThisExampleSucksBecauseStringIsFinal.class) String thing
			if (options.containsKey(SerSubclasses.class) || options.containsKey(SerComplexSubClass.class) || options.containsKey(SerComplexSubClasses.class))
				return SubclassInfo.create(source, fieldType, genericType, options, annotatedType);

			// Object / int / Object[] / int[]
			if (genericType instanceof Class<?> clazz) {
				if (clazz.isArray())
					return ArrayInfo.create(source, clazz, options, create(source, clazz.getComponentType(), null, null));
				else
					return ClassInfo.create(clazz, options);
			}

			//Thing<T,T>
			if (genericType instanceof ParameterizedType type)
				return ParameterizedClassInfo.create(options, source, type, (AnnotatedParameterizedType) annotatedType);

			//T thing
			if (genericType instanceof TypeVariable typeVariable) {
				if (source instanceof ParameterizedClassInfo info) {
					var typeName = typeVariable.getName();
					var classInfo = info.types.get(typeName);
					if (classInfo != null)
						return TypeClassInfo.create(source, classInfo.clazz, classInfo.annotations, typeName, ScanUtils.getClazz(typeVariable.getBounds()[0]), classInfo);
				}

				return UNKNOWN_INFO;
			}

			//T[] arrrrrrrr
			if (genericType instanceof GenericArrayType genericArrayType) {
				AnnotatedType annotatedArrayType;
				if (annotatedType instanceof AnnotatedArrayType type) annotatedArrayType = type.getAnnotatedGenericComponentType();
				else annotatedArrayType = null;

				return ArrayInfo.create(source, fieldType, options, create(source, fieldType, genericArrayType.getGenericComponentType(), annotatedArrayType));
			}

			//<?>
			if (genericType instanceof WildcardType wildcardType) {
				if (wildcardType.getLowerBounds().length == 0 && wildcardType.getUpperBounds().length == 1 && wildcardType.getUpperBounds()[0] == Object.class)
					return UNKNOWN_INFO;

				throw ThrowHandler.fatal(NotYetImplementedException::new, "Can't handle wildcards yet",
						ThrowEntry.of("GenericType", wildcardType),
						ThrowEntry.of("UpperBounds", Arrays.toString(wildcardType.getUpperBounds())),
						ThrowEntry.of("LowerBounds", Arrays.toString(wildcardType.getLowerBounds()))
				);
			}


			throw ThrowHandler.fatal(IllegalArgumentException::new, "Unsupported generic type",
					ThrowEntry.of("GenericType", genericType)
			);
		} catch (HyphenException hyphenException) {
			throw hyphenException.addParent(source);
		}
	}


	public void scan(Class<?> clazz) {
		this.createSerializeMetadata(ClassInfo.create(clazz, Map.of()));

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

		SerializerMetadata serializerMetadata = this.createSerializeMetadataInternal(typeInfo);
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
