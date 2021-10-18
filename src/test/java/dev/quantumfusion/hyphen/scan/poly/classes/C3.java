package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.stream.Stream;

@TestThis
public class C3<C, D> extends C2<C> {
	@Data
	public D d;

	public C3(C c, C b1, D d) {
		super(c, b1);
		this.d = d;
	}

	public static <CD> Stream<? extends C3<CD, CD>> generateC3(Stream<? extends CD> stream) {
		return stream.map(b -> new C3<>(b, b, b));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		C3<?, ?> c3 = (C3<?, ?>) o;

		return this.d.equals(c3.d);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + this.d.hashCode();
		return result;
	}

	@Override
	public String toString() {
		assert this.getClass() == C3.class;
		return "C3{" +
				"a=" + this.a +
				", b=" + this.b +
				", d=" + this.d +
				'}';
	}
}
