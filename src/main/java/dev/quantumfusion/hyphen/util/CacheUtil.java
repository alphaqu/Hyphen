package dev.quantumfusion.hyphen.util;

import java.util.Map;
import java.util.function.Supplier;

public class CacheUtil {
	private static final boolean CACHE = false;
	private static int SAVED = 0;
	private static int PULLED = 0;

	public static <R, P> R cache(Map<P, R> cache, P param, Supplier<R> func) {
		PULLED++;
		if (CACHE) {
			if (cache.containsKey(param)) {
				SAVED++;
				return cache.get(param);
			}
			final R apply = func.get();
			cache.put(param, apply);
			return apply;
		}
		return func.get();
	}

	public static void printCacheStatistics() {
		System.out.println(SAVED + " / " + PULLED + " were cached. That is " + Math.round(((float) SAVED / PULLED) * 100) + "%");
	}
}
