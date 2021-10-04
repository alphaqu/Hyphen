package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Clazzifier;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class Unknown extends Clazz {
	public Unknown() {
		super(null);
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

	public Field[] getFields() {
		return new Field[0];
	}

	public Clazz getSub(Class<?> clazz) {
		return Clazzifier.create(clazz, this);
	}

	public Clazz getType(String type) {
		return Clazzifier.UNKNOWN;
	}

	@Override
	public String toString() {
		return "UNKNOWN";
	}
}
