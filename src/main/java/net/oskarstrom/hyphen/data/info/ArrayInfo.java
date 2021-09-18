package net.oskarstrom.hyphen.data.info;

import net.oskarstrom.hyphen.ScanHandler;
import net.oskarstrom.hyphen.data.metadata.ArraySerializerMetadata;
import net.oskarstrom.hyphen.data.metadata.SerializerMetadata;
import net.oskarstrom.hyphen.util.Color;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArrayInfo extends TypeInfo {
	public final TypeInfo values;

	public ArrayInfo(Class<?> clazz, Map<Class<Annotation>, Annotation>  annotations, TypeInfo values) {
		super(clazz, annotations);
		this.values = values;
	}


	@Override
	public SerializerMetadata createMetadata(ScanHandler factory) {
		return new ArraySerializerMetadata(this, values);
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
		return new ArrayInfo(clazz, new HashMap<>(annotations), values.copy());
	}
}
