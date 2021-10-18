package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.annotations.NullableData;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.util.TestSupplierUtil;
import dev.quantumfusion.hyphen.scan.poly.classes.WrappedC1;
import dev.quantumfusion.hyphen.util.TestThis;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.INTEGERS;

@Data
@TestThis
public class NullableC1OfC1 {
	@Nullable
	@NullableData
	@DataSubclasses({C1.class, WrappedC1.class})
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

	public static Stream<? extends NullableC1OfC1> generate() {
		Supplier<? extends Stream<? extends C1<Integer>>> c1Supplier = C1.generateC1(INTEGERS);

		return TestSupplierUtil.<C1<C1<Integer>>>nullableSubClasses(
				C1.generateC1(c1Supplier),
				WrappedC1.generateWrappedC1(c1Supplier)
		).get().map(NullableC1OfC1::new);
	}

	@Override
	public String toString() {
		return "NullableC1OfC1{" +
				"data=" + this.data +
				'}';
	}
}
