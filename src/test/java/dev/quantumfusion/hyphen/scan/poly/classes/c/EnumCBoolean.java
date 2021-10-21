package dev.quantumfusion.hyphen.scan.poly.classes.c;

import java.util.function.Supplier;
import java.util.stream.Stream;

public enum EnumCBoolean implements CM1 {
	TRUE, FALSE;

	public static Supplier<Stream<? extends EnumCBoolean>> generateEnumCBoolean() {
		return () -> Stream.of(EnumCBoolean.values());
	}
}
