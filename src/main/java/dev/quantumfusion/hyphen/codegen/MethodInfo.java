package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.SerializerHandler;

import java.util.Arrays;
import java.util.Objects;

public class MethodInfo {
	private String name;
	public final Class<?> returnClass;
	public final Class<?>[] parameters;

	public MethodInfo(String name, Class<?> returnClass, Class<?>... parameters) {
		this.name = name;
		this.returnClass = returnClass;
		this.parameters = parameters;
	}


	public String getName() {
		return name;
	}

	public void setName(String name, /*to prevent people from changing the name*/ @SuppressWarnings("unused") CodegenHandler<?,?> handler) {
		assert handler != null;
		this.name = name;
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
