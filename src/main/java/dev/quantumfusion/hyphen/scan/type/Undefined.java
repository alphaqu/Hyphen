package dev.quantumfusion.hyphen.scan.type;


import java.util.Map;

/**
 * Unknown clazz. Crashes if we are trying to serialize this. Mostly a placeholder until the type is known.
 */
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
		return "SIXTYNINEFOURTWENTY".hashCode();
	}

	@Override
	public Clz resolve(Clazz context) {
		return this;
	}

	@Override
	public Clz map(Clz other, Map<TypeClazz, TypeClazz> types, MergeDirection mergeDirection) {
		return other;
	}
}
