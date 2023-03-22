package dev.notalpha.hyphen.test.simple.buffer;

import dev.notalpha.hyphen.util.TestThis;

import java.nio.LongBuffer;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.notalpha.hyphen.util.TestSupplierUtil.cross;
import static dev.notalpha.hyphen.util.TestSupplierUtil.ints;

@TestThis
public class LongBufferTest {
	public LongBuffer data;

	public LongBufferTest(LongBuffer data) {
		this.data = data;
	}

	public static Supplier<Stream<? extends LongBufferTest>> generateLongBufferTest() {
		return cross(ints(75, 30, 0, 16),ints ->   {
			long[] out = new long[ints.length + 2];
			out[0] = Long.MAX_VALUE;
			out[1] = Long.MIN_VALUE;
			for (int i = 0; i < ints.length; i++) {
				out[i + 2] = ints[i];
			}

			return new LongBufferTest(LongBuffer.wrap(out));
		});
	}

	@Override
	public String toString() {
		return "LongBufferTest{" +
				"data=" + data +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LongBufferTest that = (LongBufferTest) o;
		return Objects.equals(data, that.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(data);
	}
}