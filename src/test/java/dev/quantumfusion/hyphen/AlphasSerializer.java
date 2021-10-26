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
		Test data = new Test(453);
		test(data);
	}

	public static <O> void test(O data) {
		var factory = SerializerFactory.createDebug(UnsafeIO.class, (Class<O>) data.getClass());
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
	public static final class Test {
		public final int strings;
		private transient int[] stringArray;

		public Test(int strings) {
			this.strings = strings;
		}

		public int strings() {
			return strings;
		}

		public int[] stringArray() {
			return stringArray;
		}
	}
}
