package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.scan.ClzCreator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class MapUtil {

	public static <K, V> void forEachIndex(Map<K, V> map, IndexedConsumer<K, V> consumer) {
		int i = 0;
		for (var entry : map.entrySet()) {
			consumer.consume(entry.getKey(), entry.getValue(), i++);
		}
	}

	public static <K, V, VO> Map<K, VO> mapValues(Map<K, V> map, Function<Integer, Map<K, VO>> mapProvider, Function<V, VO> mapper) {
		final Map<K, VO> out = mapProvider.apply(map.size());
		map.forEach((k, v) -> out.put(k, mapper.apply(v)));
		return out;
	}

	public static <K, V, KO> Map<KO, V> mapKeys(Map<K, V> map, Function<Integer, Map<KO, V>> mapProvider, Function<K, KO> mapper) {
		final Map<KO, V> out = mapProvider.apply(map.size());
		map.forEach((k, v) -> out.put(mapper.apply(k), v));
		return out;
	}

	@SafeVarargs
	public static <K, V> Map<K, V> merge(Supplier<Map<K, V>> creator, Map<? extends K, ? extends V>... maps) {
		final Map<K, V> out = creator.get();
		for (Map<? extends K, ? extends V> map : maps) out.putAll(map);
		return out;
	}

	public interface IndexedConsumer<K, V> {
		void consume(K key, V value, int i);
	}

}
