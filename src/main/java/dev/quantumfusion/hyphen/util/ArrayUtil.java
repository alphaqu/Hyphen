package dev.quantumfusion.hyphen.util;

import java.util.function.BiFunction;
import java.util.function.IntFunction;

public class ArrayUtil {

	public static <A, B> void dualForEach(A[] a, B[] b, DualForEach<A, B> dualForEach) {
		final int aLength = a.length;
		final int bLength = b.length;
		if (aLength != bLength)
			throw new RuntimeException("A length " + aLength + " does not match B length " + bLength);

		for (int i = 0; i < aLength; i++)
			dualForEach.apply(a[i], b[i], i);
	}

	public static <A, B> B[] map(A[] a, IntFunction<B[]> creator, BiFunction<A, Integer, B> mapper) {
		final B[] out = creator.apply(a.length);
		for (int i = 0; i < a.length; i++)
			out[i] = mapper.apply(a[i], i);
		return out;
	}

	public static <A> A[] combine(A[] a1, A[] a2, IntFunction<A[]> creator) {
		final int a1Length = a1.length;
		final int a2Length = a2.length;
		final A[] out = creator.apply(a1Length + a2Length);
		System.arraycopy(a1, 0, out, 0, a1Length);
		System.arraycopy(a2, 0, out, a1Length, a2Length);
		return out;
	}

	@FunctionalInterface
	public interface DualForEach<A, B> {
		void apply(A a, B b, int i);
	}
}
