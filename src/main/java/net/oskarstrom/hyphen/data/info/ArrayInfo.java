package net.oskarstrom.hyphen.data.info;

import net.oskarstrom.hyphen.ScanHandler;
import net.oskarstrom.hyphen.data.metadata.ArraySerializerMetadata;
import net.oskarstrom.hyphen.data.metadata.SerializerMetadata;
import net.oskarstrom.hyphen.thr.ClassScanException;
import net.oskarstrom.hyphen.thr.ThrowHandler;
import net.oskarstrom.hyphen.util.Color;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.oskarstrom.hyphen.thr.ThrowEntry.of;

public class ArrayInfo extends TypeInfo {
	public final TypeInfo values;

	public ArrayInfo(Class<?> clazz, Map<Class<Annotation>, Annotation> annotations, TypeInfo values) {
		super(clazz, annotations);
		this.values = values;
	}

	public static ArrayInfo create(TypeInfo source, Class<?> clazz, Map<Class<Annotation>, Annotation> annotations, TypeInfo values) {
		if (values == ScanHandler.UNKNOWN_INFO)
			throw ThrowHandler.fatal(ClassScanException::new, "Type could not be identified",
					of("Source Class", source.clazz.getName()),
					of("Error Class", clazz.getName()));
		return new ArrayInfo(clazz, annotations, values);
	}


	@Override
	public SerializerMetadata createMetadata(ScanHandler factory) {
		return new ArraySerializerMetadata(this, values.createMetadata(factory));
	}

	@Override
	public String toFancyString() {
		return this.values.toFancyString() + Color.YELLOW + "[]";
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

	@Override
	public ArrayInfo copy() {
		return ArrayInfo.create(this, clazz, new HashMap<>(annotations), values.copy());
	}
}
