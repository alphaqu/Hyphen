package dev.quantumfusion.hyphen.util;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class TestSupplierUtil {
	public static final Supplier<? extends Stream<? extends String>> STRINGS = () -> Arrays.stream(new String[]{
			"UWU",
			"uwu",
			"69696969696969696969696969696969696969696969696969696969696969696969696969696969696969696969696969696969",
			""
			// TODO add unicodes
			// , "☃️"
	});

	public static final Supplier<? extends IntStream> INTS = () -> IntStream.of(
			0, 1, 2,
			-1, -2,
			Integer.MAX_VALUE,
			Integer.MIN_VALUE);

	public static final Supplier<? extends Stream<? extends Integer>> INTEGERS = () -> INTS.get().mapToObj(i -> i);

	public static final Supplier<Stream<? extends Float>> FLOATS = () -> Arrays.stream(new Float[]{
			0.0f, 0.5f, 1.0f, 2.0f,
			-0.0f, -0.5f, -1.0f, -2.0f,
			Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_NORMAL,
			-Float.MAX_VALUE, -Float.MIN_VALUE, -Float.MIN_NORMAL,
			Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY
	});

	public static final Supplier<Stream<? extends Number>> NUMBERS_IF = TestSupplierUtil.subClasses(INTEGERS, FLOATS);

	/**
	 * given a stream, will limit the length to approx sqrt(stream.count)
	 */
	public static <T> Supplier<Stream<? extends T>> reduce(Supplier<? extends Stream<? extends T>> stream, int seed) {
		Random random = new Random(seed);
		return () -> {
			// reset the count for each stream
			AtomicInteger ai = new AtomicInteger();
			return stream.get().filter(i -> {
				double v = random.nextDouble();
				return v * v * ai.incrementAndGet() < 1;
			});
		};
	}

	/**
	 * given a stream, will limit the length to approx powth-rooth(stream.count)
	 */
	public static <T> Supplier<Stream<? extends T>> reduce(Supplier<? extends Stream<? extends T>> stream, int seed, int pow) {
		Random random = new Random(seed);
		return () -> {
			// reset the count for each stream
			AtomicInteger ai = new AtomicInteger();
			return stream.get().filter(i -> {
				double v = random.nextDouble();
				return Math.pow(v, pow) * ai.incrementAndGet() < 1;
			});
		};
	}

	@SafeVarargs
	public static <T> Supplier<Stream<? extends T>> subClasses(Supplier<? extends Stream<? extends T>>... subclasses) {
		return () -> Arrays.stream(subclasses).flatMap(Supplier::get);
	}

	@SafeVarargs
	public static <T> Supplier<Stream<? extends T>> nullableSubClasses(Supplier<? extends Stream<? extends T>>... subclasses) {
		return () -> Stream.concat(Stream.of((T) null), Arrays.stream(subclasses).flatMap(Supplier::get));
	}

	public static <A, T> Supplier<Stream<? extends T>> cross(
			Supplier<? extends Stream<? extends A>> aSupplier,
			Function<? super A, ? extends T> converter
	) {
		return () -> aSupplier.get().map(converter);
	}

	public static <A, B, T> Supplier<Stream<? extends T>> cross(
			Supplier<? extends Stream<? extends A>> aSupplier,
			Supplier<? extends Stream<? extends B>> bSupplier,
			BiFunction<? super A, ? super B, ? extends T> converter
	) {
		return () -> aSupplier.get().flatMap(a ->
				bSupplier.get().map(b ->
						converter.apply(a, b)));
	}

	public static <A, B, C, T> Supplier<Stream<? extends T>> cross(
			Supplier<? extends Stream<? extends A>> aSupplier,
			Supplier<? extends Stream<? extends B>> bSupplier,
			Supplier<? extends Stream<? extends C>> cSupplier,
			TriFunction<? super A, ? super B, ? super C, ? extends T> converter
	) {
		return () -> aSupplier.get().flatMap(a ->
				bSupplier.get().flatMap(b ->
						cSupplier.get().map(c ->
								converter.apply(a, b, c))));
	}

	public static <A, B, C, D, T> Supplier<Stream<? extends T>> cross(
			Supplier<? extends Stream<? extends A>> aSupplier,
			Supplier<? extends Stream<? extends B>> bSupplier,
			Supplier<? extends Stream<? extends C>> cSupplier,
			Supplier<? extends Stream<? extends D>> dSupplier,
			QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends T> converter
	) {
		return () -> aSupplier.get().flatMap(a ->
				bSupplier.get().flatMap(b ->
						cSupplier.get().flatMap(c ->
								dSupplier.get().map(d ->
										converter.apply(a, b, c, d)))));
	}

	@FunctionalInterface
	public interface TriFunction<A, B, C, T> {
		T apply(A a, B b, C c);
	}

	@FunctionalInterface
	public interface QuadFunction<A, B, C, D, T> {
		T apply(A a, B b, C c, D d);
	}
}
