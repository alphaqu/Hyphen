package dev.quantumfusion.hyphen.test.simple.buffer;

import dev.quantumfusion.hyphen.util.TestThis;

import java.nio.IntBuffer;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.ints;

@TestThis
public class IntBufferTest {
	public IntBuffer data;

	public IntBufferTest(IntBuffer data) {
		this.data = data;
	}

	public static Supplier<Stream<? extends IntBufferTest>> generateIntBufferTest() {
		return cross(ints(75, 30, 0, 16),ints -> new IntBufferTest(IntBuffer.wrap(ints)));
	}

	@Override
	public String toString() {
		return "IntBufferTest{" +
				"data=" + data +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IntBufferTest that = (IntBufferTest) o;
		return Objects.equals(data, that.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(data);
	}
}