package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;

import java.util.Arrays;
import java.util.Objects;

public class TestGen {
	@Data
	public Simple<Integer> field;

	public TestGen(Simple<Integer> field) {
		this.field = field;
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
		SerializerFactory<ByteBufferIO, TestGen> factory = SerializerFactory.createDebug(ByteBufferIO.class, TestGen.class);

		final HyphenSerializer<ByteBufferIO, TestGen> build = factory.build();
		final TestGen data = new TestGen(new Simple(new int[]{54, 234, 5423}, 69, 5, new C2<>(420, 0)));
		final int measure = build.measure(data);
		final ByteBufferIO byteBufferIO = ByteBufferIO.create(measure * 10);
		build.put(byteBufferIO, data);


		System.out.println(byteBufferIO.pos() + " / " + measure);
		byteBufferIO.rewind();
		final TestGen testGen = build.get(byteBufferIO);



		System.out.println(testGen.equals(data));
	}

	public static class Simple<O> {
		@Data
		public int[] thign;
		@Data
		public int thign2;
		@Data
		public O thign3;
		@Data
		@DataSubclasses({C1.class, C2.class})
		public final C1<Integer> s;

		public Simple(int[] thign, int thign2, O thign3, C1<Integer> s) {
			this.thign = thign;
			this.thign2 = thign2;
			this.thign3 = thign3;
			this.s = s;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Simple simple = (Simple) o;
			return thign2 == simple.thign2 && Objects.equals(thign3, simple.thign3) && Arrays.equals(thign, simple.thign);
		}

		@Override
		public int hashCode() {
			int result = Objects.hash(thign2, thign3);
			result = 31 * result + Arrays.hashCode(thign);
			return result;
		}
	}

}
