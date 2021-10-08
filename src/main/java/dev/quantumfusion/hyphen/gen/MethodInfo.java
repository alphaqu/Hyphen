package dev.quantumfusion.hyphen.gen;

import java.util.Arrays;
import java.util.Objects;

public class MethodInfo {
	public String name;
	public Class<?> returnClass;
	public Class<?>[] parameters;

	public MethodInfo(String name, Class<?> returnClass, Class<?>... parameters) {
		this.name = name;
		this.returnClass = returnClass;
		this.parameters = parameters;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MethodInfo that = (MethodInfo) o;
		return Objects.equals(returnClass, that.returnClass) && Arrays.equals(parameters, that.parameters);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(returnClass);
		result = 31 * result + Arrays.hashCode(parameters);
		return result;
	}
}
