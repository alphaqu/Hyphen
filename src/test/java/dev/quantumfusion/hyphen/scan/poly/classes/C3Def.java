package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.stream.Stream;
@TestThis
public class C3Def<E> extends C3<E, String> {
	@Data
	public E e;

	public C3Def(E e, E b1, String s, E e1) {
		super(e, b1, s);
		this.e = e1;
	}

	public static <E> Stream<? extends C3Def<E>> generateC3Def(Stream<? extends E> stream) {
		return stream.map(e1 -> new C3Def<>(e1, e1, String.valueOf(e1), e1));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		C3Def<?> c3Def = (C3Def<?>) o;

		return this.e.equals(c3Def.e);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + this.e.hashCode();
		return result;
	}

	@Override
	public String toString() {
		assert this.getClass() == C3Def.class;
		return "C3Def{" +
				"a=" + this.a +
				", b=" + this.b +
				", d=" + this.d +
				", e=" + this.e +
				'}';
	}
}
