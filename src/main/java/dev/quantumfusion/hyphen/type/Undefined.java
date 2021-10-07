package dev.quantumfusion.hyphen.type;

public class Undefined implements Clz {
	public static final Undefined UNDEFINED = new Undefined();

	private Undefined() {
	}

	public Class<?> pullClass() {
		return Object.class;
	}

	public Class<?> pullBytecodeClass() {
		return Object.class;
	}

	@Override
	public String toString() {
		return "UNDEFINED";
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

	@Override
	public Clz resolve(Clazz source) {
		return this;
	}
}
