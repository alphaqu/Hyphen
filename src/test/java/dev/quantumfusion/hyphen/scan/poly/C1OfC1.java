package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.WrappedC1;

import java.util.Objects;

public class C1OfC1 {
	@Serialize
	@SerSubclasses({C1.class, WrappedC1.class})
	public C1<C1<Integer>> data;

	public C1OfC1(C1<C1<Integer>> data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof C1OfC1)) return false;
		C1OfC1 c1OfC1 = (C1OfC1) o;
		return Objects.equals(data, c1OfC1.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(data);
	}
}
