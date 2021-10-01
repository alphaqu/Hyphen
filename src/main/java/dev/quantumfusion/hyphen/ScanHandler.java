package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.FieldEntry;
import dev.quantumfusion.hyphen.codegen.def.MethodCallDef;
import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.info.*;
import dev.quantumfusion.hyphen.thr.ThrowEntry;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.util.ArrayType;
import dev.quantumfusion.hyphen.util.ScanUtils;
import dev.quantumfusion.hyphen.util.TypeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ScanHandler {
	public static final TypeInfo UNKNOWN_INFO = WildcardInfo.UNKNOWN;
	public final Map<TypeInfo, MethodMetadata> methods;
	public final Map<Class<?>, Function<? super TypeInfo, ? extends SerializerDef>> implementations;
	public final Map<Object, List<Class<?>>> subclasses;
	@Nullable
	private final DebugHandler debugHandler;

	protected ScanHandler(Map<TypeInfo, MethodMetadata> methods, Map<Class<?>, Function<? super TypeInfo, ? extends SerializerDef>> implementations, Map<Object, List<Class<?>>> subclasses, boolean debug) {
		this.implementations = implementations;
		this.subclasses = subclasses;
		this.debugHandler = debug ? new DebugHandler(this) : null;
		this.methods = methods;
	}

	private static void validateInput(@Nullable Class<?> clazz, @Nullable Type type, @Nullable AnnotatedType annotatedType) {

		try {
			assert annotatedType == null || type == null || annotatedType.getType() == type;
			Class<?> clazzOrNull = ScanUtils.getClazzOrNull(type);
			assert clazz == null || clazz == clazzOrNull || clazzOrNull == null;
		} catch (IllegalArgumentException ignored) {
		} catch (AssertionError assertionError) {
			throw ThrowHandler.fatal(AssertionError::new, "input validation failed",
					ThrowEntry.of("Class", clazz),
					ThrowEntry.of("Type", type),
					ThrowEntry.of("AnnotatedType", annotatedType)
			);
		}
	}

	public TypeInfo create(TypeInfo source, Class<?> clazz) {
		return this.create(source, clazz, clazz, null);
	}

	public TypeInfo create(@NotNull TypeInfo source, @Nullable Class<?> clazz, @Nullable Type rawType, @Nullable AnnotatedType annotatedType) {
		validateInput(clazz, rawType, annotatedType);

		if (rawType == null) rawType = annotatedType == null ? clazz : annotatedType.getType();
		if (clazz == null) clazz = ScanUtils.getClazzOrNull(rawType);

		Type genericType = TypeUtil.applyType(rawType, annotatedType);

		if (genericType instanceof TypeVariable type)
			return TypeClassInfo.createType(this, source, clazz, type, annotatedType);

		if (genericType instanceof ArrayType type) return ArrayInfo.createType(this, source, type, annotatedType);

		if (genericType instanceof GenericArrayType type)
			return ArrayInfo.createGenericType(this, source, clazz, type, annotatedType);

		Map<Class<? extends Annotation>, Annotation> annotations = ScanUtils.getAnnotations(source, annotatedType);
		// @Subclasses(SuperString.class, WaitThisExampleSucksBecauseStringIsFinal.class) String thing
		if (SubclassInfo.check(annotations))
			return SubclassInfo.create(this, source, clazz, rawType, annotatedType, annotations);

		if (genericType instanceof Class<?> type)
			return ClassInfo.createType(this, source, type, annotatedType, annotations);

		if (genericType instanceof ParameterizedType type)
			return ParameterizedInfo.createType(this, source, clazz, type, annotatedType, annotations);


		//<?>
		if (genericType instanceof WildcardType type) {
			return WildcardInfo.createType(this, source, clazz, type, annotatedType, annotations);
		}

		throw ThrowHandler.fatal(IllegalArgumentException::new, "Unsupported generic type",
				ThrowEntry.of("GenericType", genericType)
		);
	}


	public void scan(Class<?> clazz) {
		this.createSerializeMethod(new ClassInfo(clazz, Map.of(), true));

		if (debugHandler != null) {
			debugHandler.printMethods(methods);
		}
	}

	private MethodMetadata createSerializeMetadataInternal(TypeInfo typeInfo) {
		return typeInfo.createMetadata(this);
	}

	public MethodMetadata createSerializeMetadata(TypeInfo typeInfo) {
		if (this.methods.containsKey(typeInfo))
			return this.methods.get(typeInfo);

		return this.createSerializeMetadataInternal(typeInfo);
	}

	public void createSerializeMethod(TypeInfo typeInfo) {
		this.methods.put(typeInfo, createSerializeMetadata(typeInfo));
	}

	public SerializerDef getDefinition(FieldEntry field, ClassInfo source) {
		return getDefinition(field, field.clazz(), source);
	}

	public SerializerDef getDefinition(@Nullable FieldEntry field, TypeInfo classInfo, ClassInfo source) {
		if (!(classInfo instanceof SubclassInfo)) {
			final Class<?> clazz = classInfo.getClazz();
			final Class<?>[] classes = ScanUtils.pathTo(clazz, implementations::containsKey, TypeUtil::getInheritedClasses, 0);
			if (classes != null) {
				return implementations.get(classes.length > 0 ? classes[classes.length - 1] : clazz).apply(classInfo);
			}
		}
		if (field != null) {
			//check if field is legal
			//we don't do this on the serializerDef because they might do some grandpa 360 no-scopes on fields and access them another way
			ScanUtils.checkAccess(field.modifier(), () -> ThrowHandler.fieldAccessFail(field, source));
		}

		this.createSerializeMethod(classInfo);
		return new MethodCallDef(classInfo);
	}
}
