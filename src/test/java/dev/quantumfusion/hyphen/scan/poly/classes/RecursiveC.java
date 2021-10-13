package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.annotations.Data;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class RecursiveC<T> extends C1<T> {
	@Data
	@DataSubclasses({C1.class, RecursiveC.class})
	public C1<T> foo;

	public RecursiveC(T t, C1<T> foo) {
		super(t);
		this.foo = foo;
	}

	public static <T> Stream<? extends RecursiveC<T>> generate(
			Supplier<? extends Stream<? extends T>> tSupplier,
			int depth
	) {
		Supplier<? extends Stream<? extends C1<T>>> foos;
		if(depth <= 0){
			foos = () -> generate(tSupplier);
		} else {
			foos = TestSupplierUtil.subClasses(() -> generate(tSupplier), () -> generate(tSupplier, depth-1));
		}

		return tSupplier.get().flatMap(t ->
					foos.get().map(foo -> new RecursiveC<>(t, foo))
				);
	}

	@Override
	public String toString() {
		return "RecursiveC{" +
				"a=" + this.a +
				", foo=" + this.foo +
				'}';
	}
}

