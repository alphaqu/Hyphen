package dev.quantumfusion.hyphen.scan.poly.classes.c;

import java.util.function.Supplier;
import java.util.stream.Stream;

public enum EnumCSingleton implements CM1 {
	SINGLETON;

	public static Supplier<Stream<? extends EnumCSingleton>> generateEnumCSingleton() {
		return () -> Stream.of(EnumCSingleton.values());
	}
}
