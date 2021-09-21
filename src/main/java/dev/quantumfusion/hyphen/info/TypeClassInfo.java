package dev.quantumfusion.hyphen.info;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.gen.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.UnknownTypeException;
import dev.quantumfusion.hyphen.util.ScanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.TypeVariable;
import java.util.Map;

import static dev.quantumfusion.hyphen.thr.ThrowEntry.of;

public class TypeClassInfo extends TypeInfo {
	private final String typeName;
	private final Class<?> type;
	private final TypeInfo actual;

	public TypeClassInfo(Class<?> clazz, Map<Class<Annotation>, Annotation> annotations, String typeName, Class<?> type, TypeInfo actual) {
		super(clazz, annotations);
		this.typeName = typeName;
		this.type = type;
		this.actual = actual;
	}

	public static TypeInfo create(TypeInfo source, Class<?> clazz, TypeVariable<?> typeVariable) {
		if (source instanceof ParameterizedClassInfo info) {
			var typeName = typeVariable.getName();
			var classInfo = info.types.get(typeName);
			if (classInfo == ScanHandler.UNKNOWN_INFO)
				throw ThrowHandler.fatal(UnknownTypeException::new, "Type could not be identified",
						of("Source Class", source.clazz.getName()),
						of("Error Class", clazz));
			if (classInfo != null)
				return new TypeClassInfo(classInfo.clazz, classInfo.annotations, typeName, ScanUtils.getClazz(typeVariable.getBounds()[0]), classInfo);
		}
		return ScanHandler.UNKNOWN_INFO;
	}

	@Override
	public String toFancyString() {
		return this.actual.toFancyString();
	}

	@Override
	public String getMethodName(boolean absolute) {
		return typeName + "?" + actual.getMethodName(absolute);
	}

	@Override
	public SerializerMetadata createMetadata(ScanHandler factory) {
		return factory.createSerializeMetadata(this.actual);
	}

	@Override
	public Class<?> getClazz() {
		return actual.getClazz();
	}

	@Override
	public Class<?> getRawClass() {
		return type;
	}
}
