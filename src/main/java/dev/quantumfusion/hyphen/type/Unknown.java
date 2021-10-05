package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;
import dev.quantumfusion.hyphen.util.AnnoUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Unknown clazz. Crashes if we are trying to serialize this. Mostly a placeholder until the type is known.
 */
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

	@Override
	public boolean equals(Object o) {
		return this == o;
	}

	@Override
	public int hashCode() {
		//noinspection SpellCheckingInspection
		return "SIXTYNINEFOURTWENTY".hashCode();
	}
}
