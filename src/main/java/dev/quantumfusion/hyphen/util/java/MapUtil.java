package dev.quantumfusion.hyphen.util.java;

import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class MapUtil {

	public static <K, V> void forEachIndex(Map<K, V> map, IndexedConsumer<K, V> consumer) {
		int i = 0;
		for (var entry : map.entrySet()) {
			consumer.consume(entry.getKey(), entry.getValue(), i++);
		}
	}

	public static <K, V, VO, M extends Map<K,VO>> M mapValues(Map<K, V> map, IntFunction<? extends M> mapProvider, Function<? super V, ? extends VO> mapper) {
		final M out = mapProvider.apply(map.size());
		map.forEach((k, v) -> out.put(k, mapper.apply(v)));
		return out;
	}

	public static <K, V, KO, M extends Map<KO,V>> M mapKeys(Map<K, V> map, IntFunction<? extends M> mapProvider, Function<? super K, ? extends KO> mapper) {
		final M out = mapProvider.apply(map.size());
		map.forEach((k, v) -> out.put(mapper.apply(k), v));
		return out;
	}

	public static <K, V, VO, M extends Map<K,VO>> M mapValuesIndexed(Map<K, V> map, IntFunction<? extends M> mapProvider, IndexedFunction<? super V, ? extends VO> mapper) {
		final M out = mapProvider.apply(map.size());
		int i = 0;
		for (Map.Entry<K, V> entry : map.entrySet()) {
			out.put(entry.getKey(), mapper.apply(entry.getValue(), i++));
		}
		return out;
	}

	public static <K, V, KO, M extends Map<KO,V>> M mapKeysIndexed(Map<K, V> map, IntFunction<? extends M> mapProvider, IndexedFunction<? super K, ? extends KO> mapper) {
		final M out = mapProvider.apply(map.size());
		int i = 0;
		for (Map.Entry<K, V> entry : map.entrySet()) {
			out.put(mapper.apply(entry.getKey(), i++), entry.getValue());
		}
		return out;
	}

	@SafeVarargs
	public static <K, V> Map<K, V> merge(Supplier<Map<K, V>> creator, Map<? extends K, ? extends V>... maps) {
		final Map<K, V> out = creator.get();
		for (Map<? extends K, ? extends V> map : maps) out.putAll(map);
		return out;
	}

	@FunctionalInterface
	public interface IndexedConsumer<A, B> {
		void consume(A a, B b, int i);
	}

	@FunctionalInterface
	public interface IndexedFunction<A, Z> {
		Z apply(A a, int i);
	}

	@FunctionalInterface
	public interface IndexedBiFunction<A, B, Z> {
		Z apply(A a, B b, int i);
	}
}
