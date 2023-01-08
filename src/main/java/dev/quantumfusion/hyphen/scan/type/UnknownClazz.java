package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.FieldEntry;

import java.util.List;

public class UnknownClazz extends Clazz {
	public static final Clazz UNKNOWN = new UnknownClazz();

	private UnknownClazz() {
		super(null, Object.class);
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
	public String toString() {
		return "UNKNOWN";
	}
}
