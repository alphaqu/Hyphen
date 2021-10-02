package dev.quantumfusion.hyphen.scan.poly;

import dev.quantumfusion.hyphen.annotation.SerNull;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.scan.poly.classes.*;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.scan.poly.classes.TestSupplierUtil.INTEGERS;

public class NullableC1OfC1 {
	@Serialize
	@SerNull
	@Nullable
	@SerSubclasses({C1.class, WrappedC1.class})
	public C1<C1<Integer>> data;

	public NullableC1OfC1(@Nullable C1<C1<Integer>> data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NullableC1OfC1)) return false;
		NullableC1OfC1 c1OfC1 = (NullableC1OfC1) o;
		return Objects.equals(data, c1OfC1.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(data);
	}

	public static Stream<? extends NullableC1OfC1> generate(){
		Supplier<Stream<? extends C1<Integer>>> c1Supplier = () -> C1.generate(INTEGERS);

		return TestSupplierUtil.<C1<C1<Integer>>>nullableSubClasses(
				() -> C1.generate(c1Supplier),
				() -> WrappedC1.generate2(c1Supplier)
		).get().map(NullableC1OfC1::new);
	}

	@Override
	public String toString() {
		return "NullableC1OfC1{" +
				"data=" + this.data +
				'}';
	}
}
