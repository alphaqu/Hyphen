package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C3Ignore;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.*;

@Data
@TestThis
public class MissingTypeIgnore {
	@DataSubclasses({C1.class, C3Ignore.class})
	public C1<Integer> integer;


	public MissingTypeIgnore(C1<Integer> integer) {
		this.integer = integer;
	}

	public static Supplier<Stream<? extends MissingTypeIgnore>> generateMissingTypeIgnore() {
		return cross(subClasses(C1.generateC1(INTEGERS), C3Ignore.generateC2(INTEGERS)), MissingTypeIgnore::new);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		MissingTypeIgnore that = (MissingTypeIgnore) o;
		return Objects.equals(this.integer, that.integer);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.integer);
	}

	@Override
	public String toString() {
		return "MissingTypeIgnore{" +
				"integer=" + this.integer +
				'}';
	}
}
