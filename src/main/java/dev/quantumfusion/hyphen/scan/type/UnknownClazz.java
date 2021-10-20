package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.FieldEntry;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public class UnknownClazz extends Clazz {
	public static final Clazz UNKNOWN = new UnknownClazz();

	private UnknownClazz() {
		super(null, UnknownClazz.class);
	}

	@Override
	public List<FieldEntry> getFields() {
		return List.of();
	}

	@Override
	public int hashCode() {
		return 0xff;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}

	@Override
	public int defined() {
		return 0;
	}

	@Override
	public String toString() {
		return "UNKNOWN";
	}
}
