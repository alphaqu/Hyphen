package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.RecursiveC;
import dev.quantumfusion.hyphen.scan.poly.classes.TestSupplierUtil;

import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.scan.poly.classes.TestSupplierUtil.STRINGS;

public class RecursiveString {
	@Serialize
	@SerSubclasses({C1.class, RecursiveC.class})
	public C1<String> data;

	public RecursiveString(C1<String> data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		RecursiveString recursive = (RecursiveString) o;

		return this.data.equals(recursive.data);
	}

	@Override
	public int hashCode() {
		return this.data.hashCode();
	}

	public static Stream<? extends RecursiveString> generate(
			int depth
	) {
		return TestSupplierUtil.<C1<String>>subClasses(
						() -> C1.generate(STRINGS),
						() -> RecursiveC.generate(STRINGS, depth - 1))
				.get()
				.map(RecursiveString::new);
	}

	@Override
	public String toString() {
		return "Recursive{" +
				"data=" + this.data +
				'}';
	}
}
