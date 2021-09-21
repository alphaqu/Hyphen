package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.gen.impl.MethodCallDef;
import dev.quantumfusion.hyphen.gen.metadata.ClassSerializerMetadata;
import dev.quantumfusion.hyphen.gen.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.info.*;
import dev.quantumfusion.hyphen.thr.ThrowEntry;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.HyphenException;
import dev.quantumfusion.hyphen.thr.exception.NotYetImplementedException;
import dev.quantumfusion.hyphen.util.Color;
import dev.quantumfusion.hyphen.util.ScanUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
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
	public final Map<Object, List<Class<?>>> subclasses;
	@Nullable
	private final DebugHandler debugHandler;

	protected ScanHandler(Map<TypeInfo, SerializerMetadata> methods, Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> implementations, Map<Object, List<Class<?>>> subclasses, boolean debug) {
		this.implementations = implementations;
		this.subclasses = subclasses;
		this.debugHandler = debug ? new DebugHandler(this) : null;
		this.methods = methods;
	}

	public TypeInfo create(TypeInfo source, Class<?> clazz, @Nullable Type genericType, @Nullable AnnotatedType annotatedType) {
		if (genericType == null) genericType = clazz;
		try {
			if (source == null) {
				throw ThrowHandler.fatal(NullPointerException::new, "Source is null",
						ThrowEntry.of("ClassType", clazz),
						ThrowEntry.of("Type", genericType),
						ThrowEntry.of("AnnotatedType", annotatedType)
				);
			}

			var annotations = ScanUtils.parseAnnotations(annotatedType);

			// @Subclasses(SuperString.class, WaitThisExampleSucksBecauseStringIsFinal.class) String thing
			if (SubclassInfo.check(annotations))
				return SubclassInfo.create(this, source, clazz, genericType, annotatedType, annotations);

			// Object / int / Object[] / int[]
			if (genericType instanceof Class<?> type) {
				if (type.isArray())
					return ArrayInfo.create(source, type, annotations, create(source, type.getComponentType(), null, null));
				else
					return ClassInfo.create(type, annotations);
			}

			//Thing<T,T>
			if (genericType instanceof ParameterizedType type)
				return ParameterizedClassInfo.create(this, source, annotations, type, (AnnotatedParameterizedType) annotatedType);

			//T thing
			if (genericType instanceof TypeVariable type)
				return TypeClassInfo.create(source, clazz, type);


			//T[] arrrrrrrr
			if (genericType instanceof GenericArrayType type)
				return ArrayInfo.createGeneric(this, source, annotations, clazz, type, annotatedType);

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
