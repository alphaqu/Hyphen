package dev.quantumfusion.hyphen.test.simple.buffer;

import dev.quantumfusion.hyphen.codegen.def.BufferDef;
import dev.quantumfusion.hyphen.scan.annotations.DataBufferType;
import dev.quantumfusion.hyphen.util.TestThis;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.ints;

@TestThis
public class NativeByteBufferTest {
	@DataBufferType(BufferDef.BufferType.NATIVE)
	public ByteBuffer data;

	public NativeByteBufferTest(ByteBuffer data) {
		this.data = data;
	}

	public static Supplier<Stream<? extends NativeByteBufferTest>> generateNativeByteBufferTest() {
		return cross(ints(75, 30, 0, 16),ints -> {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			for (int anInt : ints) {
				try {
					dos.writeInt(anInt);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			return new NativeByteBufferTest(ByteBuffer.wrap(baos.toByteArray()));
		});
	}

	@Override
	public String toString() {
		return "ByteBufferTest{" +
				"data=" + data +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NativeByteBufferTest that = (NativeByteBufferTest) o;
		return Objects.equals(data, that.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(data);
	}
}