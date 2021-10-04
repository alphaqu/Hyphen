package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;
import dev.quantumfusion.hyphen.util.AnnoUtil;

import java.lang.reflect.Type;
import java.util.Map;

public class Unknown extends Clazz {
	public Unknown() {
		super(null, Map.of(), Map.of());
	}

	public Class<?> pullClass() {
		return Object.class;
	}

	public Class<?> pullBytecodeClass() {
		return Object.class;
	}

	public Type getSuper() {
		return new Type() {
			@Override
			public String getTypeName() {
				return "UNKNOWN";
			}
		};
	}

	public Clazz getSub(Class<?> clazz) {
		return Clazzifier.create(AnnoUtil.wrap(clazz), this);
	}

	public Clazz defineType(String type) {
		return Clazzifier.UNKNOWN;
	}

	@Override
	public String toString() {
		return "UNKNOWN";
	}
}
