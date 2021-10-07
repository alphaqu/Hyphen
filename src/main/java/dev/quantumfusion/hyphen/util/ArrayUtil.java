package dev.quantumfusion.hyphen.util;

import java.util.Arrays;
import java.util.function.*;

public class ArrayUtil {

	public static <A, B> void dualForEach(A[] a, B[] b, DualForEach<? super A, ? super B> dualForEach) {
		final int aLength = a.length;
		final int bLength = b.length;
		if (aLength != bLength)
			throw new RuntimeException("A length " + aLength + " does not match B length " + bLength);

		for (int i = 0; i < aLength; i++)
			dualForEach.apply(a[i], b[i], i);
	}

	public static <A, B> void dualFor(A[] a, B[] b, BiConsumer<? super A, ? super B> dualFor) {
		final int length = a.length;
		if (length != b.length) {
			throw new RuntimeException("A length " + length + " does not match B length " + b.length);
		}
		for (int i = 0; i < length; i++) {
			dualFor.accept(a[i], b[i]);
		}
	}

	public static <A, B> B[] map(A[] a, IntFunction<B[]> creator, IndexedMap<? super A, ? extends B> mapper) {
		final B[] out = creator.apply(a.length);
		for (int i = 0; i < a.length; i++)
			out[i] = mapper.apply(a[i], i);
		return out;
	}

	public static <A, B> B[] map(A[] a, IntFunction<B[]> creator, Function<? super A, ? extends B> mapper) {
		final B[] out = creator.apply(a.length);
		for (int i = 0; i < a.length; i++) {
			out[i] = mapper.apply(a[i]);
		}
		return out;
	}

	public static <A, B, D> B[] map(A[] a, IntFunction<B[]> creator, D data, BiFunction<? super A, ? super D, ? extends B> mapper) {
		final B[] out = creator.apply(a.length);
		for (int i = 0; i < a.length; i++) {
			out[i] = mapper.apply(a[i], data);
		}
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

	public static <T> T[] filter(T[] array, Predicate<? super T> predicate) {
		if (array.length == 0)
			return array;
		int count = 0;
		T[] clone = array.clone();
		for (T t : array) {
			if (predicate.test(t))
				clone[count++] = t;
		}
		return Arrays.copyOf(clone, count);
	}

	@FunctionalInterface
	public interface DualForEach<A, B> {
		void apply(A a, B b, int i);
	}

	@FunctionalInterface
	public interface IndexedMap<A, B> {
		B apply(A a, int i);
	}
}
