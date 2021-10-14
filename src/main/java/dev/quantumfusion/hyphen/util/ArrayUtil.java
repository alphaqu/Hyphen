package dev.quantumfusion.hyphen.util;

import java.util.function.BiConsumer;

public class ArrayUtil {

	public static <A, B> void dualFor(A[] a, B[] b, BiConsumer<A, B> consumer) {
		for (int i = 0; i < a.length; i++) consumer.accept(a[i], b[i]);
	}
}
