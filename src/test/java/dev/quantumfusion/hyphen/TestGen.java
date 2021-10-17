package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;

import java.util.Arrays;
import java.util.Objects;

@Data
public class TestGen {
	public Simple<Integer> field;

	public TestGen(Simple<Integer> field) {
		this.field = field;
	}

	@Data
	public static final class Simple<O> {
		public final int[] thign;
		public final int thign2;
		public final O thign3;
		public final O thign4;
		public final Simple2<O> thign5;

		public Simple(int[] thign, int thign2, O thign3, O thign4, Simple2<O> thign5) {
			this.thign = thign;
			this.thign2 = thign2;
			this.thign3 = thign3;
			this.thign4 = thign4;
			this.thign5 = thign5;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Simple<?> simple = (Simple<?>) o;
			return thign2 == simple.thign2 && Arrays.equals(thign, simple.thign) && Objects.equals(thign3, simple.thign3) && Objects.equals(thign4, simple.thign4) && Objects.equals(thign5, simple.thign5);
		}

		@Override
		public int hashCode() {
			int result = Objects.hash(thign2, thign3, thign4, thign5);
			result = 31 * result + Arrays.hashCode(thign);
			return result;
		}

		public int[] thign() {
			return thign;
		}

		public int thign2() {
			return thign2;
		}

		public O thign3() {
			return thign3;
		}

		public O thign4() {
			return thign4;
		}

		public Simple2<O> thign5() {
			return thign5;
		}

		@Override
		public String toString() {
			return "Simple[" +
						   "thign=" + thign + ", " +
						   "thign2=" + thign2 + ", " +
						   "thign3=" + thign3 + ", " +
						   "thign4=" + thign4 + ", " +
						   "thign5=" + thign5 + ']';
		}

	}

	@Data
	public static final class Simple2<O> {
		public final int[] thign;
		public final int thign2;
		public final O thign3;
		public final O thign4;
		public final O thign5;

		public Simple2(int[] thign, int thign2, O thign3, O thign4, O thign5) {
			this.thign = thign;
			this.thign2 = thign2;
			this.thign3 = thign3;
			this.thign4 = thign4;
			this.thign5 = thign5;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Simple2<?> simple2 = (Simple2<?>) o;
			return thign2 == simple2.thign2 && Arrays.equals(thign, simple2.thign) && Objects.equals(thign3, simple2.thign3) && Objects.equals(thign4, simple2.thign4) && Objects.equals(thign5, simple2.thign5);
		}

		@Override
		public int hashCode() {
			int result = Objects.hash(thign2, thign3, thign4, thign5);
			result = 31 * result + Arrays.hashCode(thign);
			return result;
		}

		public int[] thign() {
			return thign;
		}

		public int thign2() {
			return thign2;
		}

		public O thign3() {
			return thign3;
		}

		public O thign4() {
			return thign4;
		}

		public O thign5() {
			return thign5;
		}

		@Override
		public String toString() {
			return "Simple2[" +
						   "thign=" + thign + ", " +
						   "thign2=" + thign2 + ", " +
						   "thign3=" + thign3 + ", " +
						   "thign4=" + thign4 + ", " +
						   "thign5=" + thign5 + ']';
		}

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TestGen testGen = (TestGen) o;
		return Objects.equals(field, testGen.field);
	}

	@Override
	public int hashCode() {
		return Objects.hash(field);
	}

	public static void main(String[] args) {
		TestGen in = new TestGen(new Simple<Integer>(new int[]{54, 234, 5423}, 69, 5, 4, new Simple2<>(new int[]{54, 234, 5423}, 435, 2, 2345, 23)));


		var serializer = SerializerFactory.createDebug(ByteBufferIO.class, TestGen.class).build();

		for (int i = 0; i < 100; i++) {
			run(false, false);
			System.out.print(i + "\r");
		}
		for (int i = 0; i < 100; i++) {
			run(false, true);
			System.out.print(i + "\r");
		}
		run(true, true);
		run(true, false);
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
