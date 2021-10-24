package dev.quantumfusion.hyphen.util;

import java.util.Arrays;
import java.util.function.*;

/**
 * T lot of very useful code that helps with handling Arrays for better code readability.
 */
@SuppressWarnings("unchecked")
public class ArrayUtil {

	/**
	 * Loops over 2 arrays at the same time. <br>
	 * So if you have to array's that have linked content, and
	 * you want to combine the values or do something else
	 * you can use this method.<br> <br>
	 * <p>
	 * This is the version with the index included.<br>
	 * To exclude the index use {@link ArrayUtil#dualFor(Object[], Object[], BiConsumer)} instead
	 *
	 * @param a           Array 1
	 * @param b           Array 2
	 * @param dualForEach The Consumer that iterates.
	 * @param <A>         Array 1 Type
	 * @param <B>         Array 2 Type
	 * @see ArrayUtil#dualFor(Object[], Object[], BiConsumer)
	 */
	public static <A, B> void dualForEach(A[] a, B[] b, DualForEach<? super A, ? super B> dualForEach) {
		checkMatchingLength(a, b);
		for (int i = 0; i < a.length; i++)
			dualForEach.apply(a[i], b[i], i);
	}

	/**
	 * Same as {@link ArrayUtil#dualForEach(A[], B[], DualForEach)} but with the index excluded.
	 */
	public static <A, B> void dualFor(A[] a, B[] b, BiConsumer<? super A, ? super B> dualFor) {
		checkMatchingLength(a, b);
		for (int i = 0; i < a.length; i++) {
			dualFor.accept(a[i], b[i]);
		}
	}

	private static void checkMatchingLength(Object[] a, Object[] b) {
		if (a.length != b.length) {
			throw new RuntimeException("T length " + a.length + " does not match B length " + b.length);
		}
	}


	/**
	 * Map a given array to another type or something else. <br>
	 * Equivalent to {@link Arrays#stream(Object[])} and then {@link java.util.stream.Stream#map(Function)} <br>
	 * This method is however much faster and cleaner. <br> <br>
	 * <p>
	 * This version has the index included. If you want to exclude the index use <br>
	 * {@link ArrayUtil#map(Object[], IntFunction, Function)} instead.
	 */
	public static <A, B> B[] map(A[] a, IntFunction<B[]> creator, IndexedMap<? super A, ? extends B> mapper) {
		final B[] out = creator.apply(a.length);
		for (int i = 0; i < a.length; i++)
			out[i] = mapper.apply(a[i], i);
		return out;
	}

	/**
	 * Same as {@link ArrayUtil#map(Object[], IntFunction, IndexedMap)} but with the index excluded.
	 */
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

	/**
	 * Combines 2 arrays into 1.
	 *
	 * @param a1      Array 1
	 * @param a2      Array y
	 * @param creator Creates the resulting array.
	 * @param <T>     The Array Type
	 * @return The combined array.
	 */
	public static <T> T[] combine(T[] a1, T[] a2, IntFunction<T[]> creator) {
		final int a1Length = a1.length;
		final int a2Length = a2.length;
		final T[] out = creator.apply(a1Length + a2Length);
		System.arraycopy(a1, 0, out, 0, a1Length);
		System.arraycopy(a2, 0, out, a1Length, a2Length);
		return out;
	}


	/**
	 * Combines 2 arrays into 1.
	 *
	 * @param a1  Array 1
	 * @param a2  Array y
	 * @param <T> The Array Type
	 * @return The combined array.
	 */
	public static <T> T[] combine(T[] a1, T[] a2) {
		T[] out = Arrays.copyOf(a1, a1.length + a2.length);
		System.arraycopy(a2, 0, out, a1.length, a2.length);
		return out;
	}

	/**
	 * Filters the array's content.
	 *
	 * @param array     The Array
	 * @param predicate The Predicate. Matching entries will be included.
	 * @param <T>       The Array Elements.
	 * @return The Filtered array.
	 */
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

	public static <T> T[] copyAndAppend(T[] oldArray, T newEntry) {
		final int length = oldArray.length;
		var newArray = new Object[length + 1];
		System.arraycopy(oldArray, 0, newArray, 0, length);
		newArray[length] = newEntry;
		return (T[]) newArray;
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
