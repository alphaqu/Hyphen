package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.scan.struct.ParameterStruct;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructParameters {
	public final Map<String, Integer> lookup = new HashMap<>();
	public final List<ParameterStruct> list;

	public StructParameters(List<ParameterStruct> list) {
		this.list = list;
		for (int i = 0; i < list.size(); i++) {
			ParameterStruct parameter = list.get(i);
			lookup.put(parameter.typeName, i);
		}
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
}
