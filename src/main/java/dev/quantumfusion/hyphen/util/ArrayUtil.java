package dev.quantumfusion.hyphen.util;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ArrayUtil {

	public static <A, B> void dualFor(A[] a, B[] b, DualFor<A, B> dualFor) {
		final int length = a.length;
		if (length != b.length) {
			throw new RuntimeException("A length " + length + " does not match B length " + b.length);
		}
		for (int i = 0; i < length; i++) {
			dualFor.apply(a[i], b[i], i);
		}
	}

	public static <A, B> B[] map(A[] a, Function<Integer, B[]> creator, BiFunction<A, Integer, B> mapper) {
		final B[] out = creator.apply(a.length);
		for (int i = 0; i < a.length; i++) {
			out[i] = mapper.apply(a[i], i);
		}
		return out;
	}

	public static <A> A[] combine(A[] a1, A[] a2, Function<Integer, A[]> creator) {
		final int a1Length = a1.length;
		final int a2Length = a2.length;
		final A[] out = creator.apply(a1Length + a2Length);
		System.arraycopy(a1, 0, out, 0, a1Length);
		System.arraycopy(a2, 0, out, a1Length, a2Length);
		return out;
	}

	@FunctionalInterface
	public interface DualFor<A, B> {
		void apply(A a, B b, int i);
	}
}
