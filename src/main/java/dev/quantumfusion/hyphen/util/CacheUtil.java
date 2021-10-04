package dev.quantumfusion.hyphen.util;

import java.util.Map;
import java.util.function.Function;

public class CacheUtil {
	private static final boolean CACHE = true;

	public static <R, P> R cache(Map<P, R> cache, P param, Function<P, R> func) {
		if (CACHE) {
			if (cache.containsKey(param)) return cache.get(param);
			final R apply = func.apply(param);
			cache.put(param, apply);
			return apply;
		}
		return func.apply(param);
	}
}
