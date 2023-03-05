package dev.quantumfusion.hyphen.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ValueGenerator {

	public int genInt() {
		return 0;
	}

	public <O> List<O> genList(Supplier<O> generator) {
		var out = new ArrayList<O>();
		for (int i = 0; i < 10; i++) {
			out.add(generator.get());
		}
		return out;
	}
}
