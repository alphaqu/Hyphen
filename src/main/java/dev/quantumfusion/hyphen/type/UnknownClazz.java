package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.FieldEntry;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.List;

public class UnknownClazz extends Clazz{
	public static final Clazz UNKNOWN = new UnknownClazz();

	private UnknownClazz() {
		super(UnknownClazz.class, new Annotation[0]);
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
