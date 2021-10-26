package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.UnsafeIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class AlphasSerializer {

	@org.junit.jupiter.api.Test
	void name() {
		Test data = new Test("fjlfhdsakvbnivr", new String[]{"asjdffsdak", "asjdffdsalk", "asjdflk", "asjdflkfasd"});
		test(data);
	}

	public static <O> void test(O data) {
		var factory = SerializerFactory.createDebug(UnsafeIO.class, (Class<O>) data.getClass());
		factory.addGlobalAnnotation(String.class, DataNullable.class, null);
		factory.setExportDir(Path.of("./"));

		final HyphenSerializer<UnsafeIO, O> serializer = factory.build();
		final int measure = serializer.measure(data);
		final UnsafeIO unsafeIO = UnsafeIO.create(measure);
		serializer.put(unsafeIO, data);
		System.out.println(unsafeIO.pos() + " / " + measure);
		unsafeIO.rewind();
		final O test = serializer.get(unsafeIO);
		System.out.println(test.equals(data));
	}



	@Data
	public static class Test {
		public String strings;
		public String[] stringArray;

		public Test(String strings, String[] stringArray) {
			this.strings = strings;
			this.stringArray = stringArray;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Test test = (Test) o;
			return Objects.equals(strings, test.strings) && Arrays.equals(stringArray, test.stringArray);
		}

		@Override
		public int hashCode() {
			int result = Objects.hash(strings);
			result = 31 * result + Arrays.hashCode(stringArray);
			return result;
		}
	}
}
