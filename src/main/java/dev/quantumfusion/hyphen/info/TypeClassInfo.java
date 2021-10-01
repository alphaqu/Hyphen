package dev.quantumfusion.hyphen.info;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.UnknownTypeException;
import dev.quantumfusion.hyphen.util.ScanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

import static dev.quantumfusion.hyphen.thr.ThrowEntry.of;

public class TypeClassInfo extends TypeInfo {
	private final String typeName;
	private final Class<?> rawType;
	private final TypeInfo actual;

	public TypeClassInfo(Class<?> clazz, Type type, AnnotatedType annotatedType, Map<Class<? extends Annotation>, Annotation> annotations, String typeName, Class<?> rawType, TypeInfo actual) {
		super(clazz, type, annotatedType, annotations);
		this.typeName = typeName;
		this.rawType = rawType;
		this.actual = actual;
	}

	public static TypeInfo createType(ScanHandler handler, TypeInfo source, Class<?> clazz, TypeVariable<?> typeVariable, AnnotatedType annotatedType) {
		var annotations = ScanUtils.getAnnotations(source, annotatedType);
		if (source instanceof ParameterizedInfo info) {
			var typeName = typeVariable.getName();
			var classInfo = info.types.get(typeName);
			if (classInfo == ScanHandler.UNKNOWN_INFO)
				throw ThrowHandler.fatal(UnknownTypeException::new, "Type could not be identified",
						of("Source Class", source.clazz.getName()),
						of("Error Class", clazz));
			if (classInfo != null) {
				annotations.putAll(classInfo.annotations);
				// @Subclasses(SuperString.class, WaitThisExampleSucksBecauseStringIsFinal.class) String thing
				if (SubclassInfo.check(annotations))
					return SubclassInfo.create(handler, source, classInfo.clazz, classInfo.type, classInfo.annotatedType, annotations);

				return new TypeClassInfo(classInfo.clazz, typeVariable, annotatedType, annotations, typeName, ScanUtils.getClazz(typeVariable.getBounds()[0]), classInfo);
			}
		}
		return ScanHandler.UNKNOWN_INFO;
	}

	@Override
	public String toFancyString() {
		return this.actual.toFancyString();
	}

	@Override
	public String getMethodName(boolean absolute) {
		return actual.getMethodName(absolute);
	}

	@Override
	public MethodMetadata createMetadata(ScanHandler factory) {
		return factory.createSerializeMetadata(this.actual);
	}

	@Override
	public Class<?> getClazz() {
		return actual.getClazz();
	}

	@Override
	public boolean equals(Object o) {
		return actual.equals(o);
	}

	@Override
	public int hashCode() {
		return actual.hashCode();
	}

	@Override
	public Class<?> getRawType() {
		return rawType;
	}
}
