package dev.quantumfusion.hyphen.util;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
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

	public static final Supplier<? extends Stream<? extends Integer>> INTEGERS = () -> Arrays.stream(new Integer[]{
			0, 1, 2,
			-1, -2,
			/*
			100, 420, 69_420, 500_000, 123_456_789,
			-100, -420, -69_420, -500_000, -123_456_789,
			(int) Byte.MAX_VALUE,
			(int) Byte.MIN_VALUE,
			(int) Short.MAX_VALUE,
			(int) Short.MIN_VALUE,*/
			Integer.MAX_VALUE,
			Integer.MIN_VALUE
	});


	@SafeVarargs
	public static <T> Supplier<? extends Stream<? extends T>> subClasses(Supplier<? extends Stream<? extends T>>... subclasses) {
		return () -> Arrays.stream(subclasses).flatMap(Supplier::get);
	}

	@SafeVarargs
	public static <T> Supplier<? extends Stream<? extends T>> nullableSubClasses(Supplier<? extends Stream<? extends T>>... subclasses) {
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
