package dev.quantumfusion.hyphen.info;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.gen.metadata.ArraySerializerMetadata;
import dev.quantumfusion.hyphen.gen.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.ClassScanException;
import dev.quantumfusion.hyphen.util.ArrayType;
import dev.quantumfusion.hyphen.util.Color;
import dev.quantumfusion.hyphen.util.ScanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import static dev.quantumfusion.hyphen.thr.ThrowEntry.of;

public class ArrayInfo extends TypeInfo {
	private final TypeInfo values;

	public ArrayInfo(Class<?> clazz, Type type, AnnotatedType annotatedType, Map<Class<Annotation>, Annotation> annotations, TypeInfo values) {
		super(clazz, type, annotatedType, annotations);
		this.values = values;
	}

	public static ArrayInfo createType(ScanHandler handler, TypeInfo source, ArrayType arrayType, AnnotatedType annotatedType) {
		Map<Class<Annotation>, Annotation> annotations = ScanUtils.parseAnnotations(annotatedType);
		Class<?> type = arrayType.getType();
		TypeInfo values;
		if (annotatedType != null)
			values = handler.create(source, arrayType.getComponentType(), null, ((AnnotatedArrayType) annotatedType).getAnnotatedGenericComponentType());
		else values = handler.create(source, arrayType.getComponentType(), null, null);

		if (values == ScanHandler.UNKNOWN_INFO) {
			throw ThrowHandler.fatal(ClassScanException::new, "Type could not be identified",
					of("Source Class", source.clazz.getName()),
					of("Error Class", type.getName()));
		}
		return new ArrayInfo(type, arrayType, annotatedType, annotations, values);
	}

	public static ArrayInfo createGenericType(ScanHandler handler, TypeInfo source, Class<?> clazz, GenericArrayType arrayType, AnnotatedType annotatedType) {
		AnnotatedType annotatedArrayType;
		if (annotatedType instanceof AnnotatedArrayType type)
			annotatedArrayType = type.getAnnotatedGenericComponentType();
		else annotatedArrayType = null;

		TypeInfo values = handler.create(source, clazz, arrayType == null ? null : arrayType.getGenericComponentType(), annotatedArrayType);
		if (values == ScanHandler.UNKNOWN_INFO)
			throw ThrowHandler.fatal(ClassScanException::new, "Type could not be identified",
					of("Source Class", source.clazz.getName()),
					of("Error Class", clazz.getName()));

		return new ArrayInfo(clazz, arrayType, annotatedType, ScanUtils.parseAnnotations(annotatedType), values);
	}


	@Override
	public SerializerMetadata createMetadata(ScanHandler factory) {
		return new ArraySerializerMetadata(this, factory.createSerializeMetadata(this.values));
	}

	@Override
	public String toFancyString() {
		return this.values.toFancyString() + Color.YELLOW + "[]";
	}

	@Override
	public String getMethodName(boolean absolute) {
		return values.getMethodName(absolute) + "[]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ArrayInfo arrayInfo)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(values, arrayInfo.values);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), values);
	}

	@Override
	public String toString() {
		return values.toString() + "[]";
	}

}
