package dev.quantumfusion.hyphen.scan.poly.classes;

import java.util.Arrays;
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
}
