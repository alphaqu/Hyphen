package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.RecursiveC;

public class Recursive {
	@Serialize
	@SerSubclasses({C1.class, RecursiveC.class})
	public C1<String> data;

	public Recursive(C1<String> data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		Recursive recursive = (Recursive) o;

		return this.data.equals(recursive.data);
	}

	@Override
	public int hashCode() {
		return this.data.hashCode();
	}
}
