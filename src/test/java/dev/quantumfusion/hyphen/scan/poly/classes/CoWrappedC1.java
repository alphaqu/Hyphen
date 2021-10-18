package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.stream.Stream;

@TestThis
public class CoWrappedC1<A, CA extends C1<A>> extends C1<CA> {
	@Data
	public A selfA;

	public CoWrappedC1(CA ca, A selfA) {
		super(ca);
		this.selfA = selfA;
	}

	public static <A> Stream<? extends CoWrappedC1<A, C1<A>>> generateCoWrappedC1(Stream<? extends A> stream) {
		return stream.map(e1 -> new CoWrappedC1<>(new C1<>(e1), e1));
	}
}
