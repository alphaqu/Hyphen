package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataFixedArraySize;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;

import java.nio.file.Path;
import java.util.List;

public class AlphasSerializer {

	@org.junit.jupiter.api.Test
	void name() {
		final String[] thing2 = new String[]{"fg","fdas"};
		DashShader shader = new DashShader(List.of(),List.of());
		test(shader);
	}

	public static <O> void test(O data) {
		var factory = SerializerFactory.create(ByteBufferIO.class, (Class<O>) data.getClass());
		factory.setExportDir(Path.of("./"));
		factory.setOption(Options.FAST_ALLOC, false);
		try {
			final HyphenSerializer<ByteBufferIO, O> serializer = factory.build();

			final int measure = serializer.measure(data);
			final ByteBufferIO unsafeIO = ByteBufferIO.create(measure);
			serializer.put(unsafeIO, data);
			System.out.println(unsafeIO.pos() + " / " + measure);
			unsafeIO.rewind();
			final O test = serializer.get(unsafeIO);
			System.out.println(test.equals(data));
		} catch (Throwable error) {
			error.printStackTrace();
		}

	}

	@Data
	public static class DashShader {
		public final List<String> thing;
		public final List<String> thing1;


		public DashShader(List<String> thing, List<String> thing1) {
			this.thing = thing;
			this.thing1 = thing1;
		}
	}
}
