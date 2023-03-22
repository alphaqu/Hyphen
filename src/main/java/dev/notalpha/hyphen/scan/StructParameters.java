package dev.notalpha.hyphen.scan;

import dev.notalpha.hyphen.scan.struct.ParameterStruct;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StructParameters {
	public final Map<String, Integer> lookup;
	public final List<ParameterStruct> list;

	public StructParameters(List<ParameterStruct> list) {
		this.list = list;
		var lookup = new HashMap<String, Integer>();
		for (int i = 0; i < list.size(); i++) {
			ParameterStruct parameter = list.get(i);
			lookup.put(parameter.typeName, i);
		}
		this.lookup = Map.copyOf(lookup);
	}

	@NotNull
	public ParameterStruct getParameter(String name) {
		Integer index = this.lookup.get(name);
		if (index == null) {
			throw new IllegalArgumentException("Type parameter " + name + " does not exist");
		}
		return this.list.get(index);
	}

	@NotNull
	public ParameterStruct getParameter(Integer index) {
		return this.list.get(index);
	}

	public int size() {
		return list.size();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StructParameters that = (StructParameters) o;

		if (!Objects.equals(lookup, that.lookup)) return false;
		return Objects.equals(list, that.list);
	}

	@Override
	public int hashCode() {
		int result = lookup != null ? lookup.hashCode() : 0;
		result = 31 * result + (list != null ? list.hashCode() : 0);
		return result;
	}
}
