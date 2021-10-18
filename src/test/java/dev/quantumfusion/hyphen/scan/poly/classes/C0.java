package dev.quantumfusion.hyphen.scan.poly.classes;


import java.util.function.Supplier;
import java.util.stream.Stream;

public class C0 {
	@Override
	public int hashCode() {
		return 101;
	}

	@Override
	public boolean equals(Object obj) {
		return obj.getClass() == C0.class;
	}

	public static <A> Supplier<? extends Stream<? extends C0>> generateC0() {
		return () -> Stream.of(new C0());
	}

	@Override
	public String toString() {
		return "C0{}";
	}
}
