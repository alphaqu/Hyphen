package dev.quantumfusion.hyphen.scan.poly.classes;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class WrappedC1<A> extends C1<C1<A>> {
	public WrappedC1(C1<A> ac1) {
		super(ac1);
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public static <A> Stream<? extends WrappedC1<A>> generate2(Supplier<? extends Stream<? extends C1<A>>> ac1Supplier){
		return ac1Supplier.get().map(WrappedC1::new);
	}
}
