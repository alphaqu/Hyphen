package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.scan.type.Clz;
import dev.quantumfusion.hyphen.util.CacheUtil;
import dev.quantumfusion.hyphen.util.ScanUtil;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ClzCreator<R extends Clz> {
	private final Predicate<? super Class<?>> clz;
	private final BiFunction<? super AnnotatedType, ? super Clazz, ? extends R> create;

	public ClzCreator(Predicate<? super Class<?>> clz, BiFunction<? super AnnotatedType, ? super Clazz, ? extends R> create) {
		this.clz = clz;
		this.create = create;
	}

	public boolean canProcess(Class<? extends Type> type) {
		return this.clz.test(type);
	}

	public R apply(AnnotatedType annotated, Clazz context) {
		return this.create.apply(annotated, context);
	}


	// ========= factories =========

	public static <T, R extends Clz> ClzCreator<R> of(
			Class<? extends T> clz,
			BiFunction<? super AnnotatedType, ? super Clazz, ? extends R> create) {
		return new ClzCreator<R>(clz::isAssignableFrom, create);
	}

	public static <T, R extends Clz> ClzCreator<R> of(
			Class<? extends T> clz,
			Function<? super AnnotatedType, ? extends R> create) {
		return new ClzCreator<>(clz::isAssignableFrom, (t, context) -> create.apply(t));
	}

	public static <T, R extends Clz> ClzCreator<R> ofC(
			Class<? extends T> clz,
			Function<? super Class<?>, ? extends R> create) {
		return new ClzCreator<>(clz::isAssignableFrom, (t, context) -> create.apply(ScanUtil.getClassFrom(t)));
	}

	public static <T, R extends Clz> ClzCreator<R> of(
			Class<? extends T> clz,
			Supplier<? extends R> create) {
		return new ClzCreator<>(clz::isAssignableFrom, (t, context) -> create.get());
	}

	@SuppressWarnings("unchecked")
	public static <T, R extends Clz> ClzCreator<R> ofT(
			Class<? extends T> clz,
			Function<? super T, ? extends R> create) {
		return new ClzCreator<>(clz::isAssignableFrom, (t, context) -> create.apply((T) t.getType()));
	}


	public ClzCreator<R> cached(Function<? super AnnotatedType, Object> key) {
		Map<Object, R> cache = new HashMap<>();
		return new ClzCreator<>(
				this.clz,
				(ann, context) -> CacheUtil.cache(cache, key, ann, context, this.create)
		);
	}

	public ClzCreator<R> postProcess(TriConsumer<? super R, ? super AnnotatedType, ? super Clazz> consumer) {
		return new ClzCreator<>(
				this.clz,
				(type, context) -> {
					R res = this.create.apply(type, context);
					consumer.accept(res, type, context);
					return res;
				}
		);
	}

	public ClzCreator<R> cachedOrPostProcess(Function<? super AnnotatedType, Object> key, TriConsumer<? super R, ? super AnnotatedType, ? super Clazz> consumer) {
		Map<Object, R> cache = new HashMap<>();
		return new ClzCreator<>(
				this.clz,
				(ann, context) -> {
					var k = key.apply(ann);
					if (cache.containsKey(k)) return cache.get(k);
					final R res = this.create.apply(ann, context);
					cache.put(k, res);
					consumer.accept(res, ann, context);
					return res;
				}
		);
	}


	@FunctionalInterface
	public interface TriConsumer<A, B, C> {
		void accept(A a, B b, C c);
	}

}
