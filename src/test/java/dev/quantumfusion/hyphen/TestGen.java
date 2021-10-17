package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;

import java.util.Arrays;
import java.util.Objects;

@Data
public class TestGen {
	public Simple<Integer>[] field;

	public TestGen(Simple<Integer>[] field) {
		this.field = field;
	}

	@Data
	public static final class Simple<O> {
		public final O[] thign;
		public final int thign2;
		public final O thign3;

		public Simple(O[] thign, int thign2, O thign3) {
			this.thign = thign;
			this.thign2 = thign2;
			this.thign3 = thign3;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Simple<?> simple = (Simple<?>) o;
			return thign2 == simple.thign2 && Arrays.equals(thign, simple.thign) && Objects.equals(thign3, simple.thign3);
		}

		@Override
		public int hashCode() {
			int result = Objects.hash(thign2, thign3);
			result = 31 * result + Arrays.hashCode(thign);
			return result;
		}
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TestGen testGen = (TestGen) o;
		return Arrays.equals(field, testGen.field);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(field);
	}

	public static void main(String[] args) {
		final Simple<Integer> integerSimple = new Simple<>(new Integer[]{69, 420}, 432, 123);
		TestGen in = new TestGen(new Simple[]{integerSimple, integerSimple, integerSimple});
		var serializer = SerializerFactory.createDebug(ByteBufferIO.class, TestGen.class).build();
		int measure = serializer.measure(in);
		ByteBufferIO byteBufferIO = ByteBufferIO.create(measure);
		serializer.put(byteBufferIO, in);
		byteBufferIO.rewind();
		TestGen out = serializer.get(byteBufferIO);
		System.out.println(in.equals(out));
	}

	private static void run(boolean measureTime, boolean fastAlloc) {
		int iterations = 100;

		profile(measureTime, "Create", () -> {
			for (int i = 0; i < iterations; i++) {
				final SerializerFactory<ByteBufferIO, TestGen> factory = SerializerFactory.create(ByteBufferIO.class, TestGen.class);
				factory.setOption(Options.FAST_ALLOC, fastAlloc);
				factory.build();
			}
		});


	}

	private static void profile(boolean measureTime, String name, Runnable runnable) {
		if (measureTime) {
			long start = System.nanoTime();
			runnable.run();
			final long nano = System.nanoTime() - start;
			System.out.println(name + " took: " + (Math.round(nano / 1_000_0f) / 100f) + "ms");
		} else {
			runnable.run();
		}
	}

}
