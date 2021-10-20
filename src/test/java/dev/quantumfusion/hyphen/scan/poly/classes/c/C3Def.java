package dev.quantumfusion.hyphen.scan.poly.classes.c;

import dev.quantumfusion.hyphen.scan.annotations.Data;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.STRINGS;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

public class C3Def<E> extends C3<E, String> {
	@Data
	public E e;

	public C3Def(E e, E b1, String s, E e1) {
		super(e, b1, s);
		this.e = e1;
	}

	public C3Def(E e, E b1, Object s, E e1) {
		this(e, b1, (String) s, e1);
	}

	public static <E> Supplier<? extends Stream<? extends C3Def<E>>> generateC3Def(
			Supplier<? extends Stream<? extends E>> eProvider
	) {
		return cross(eProvider, eProvider, STRINGS, eProvider, C3Def::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!super.equals(o)) return false;

		C3Def<?> c3Def = (C3Def<?>) o;

		return Objects.equals(this.e, c3Def.e);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + Objects.hashCode(this.e);
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
