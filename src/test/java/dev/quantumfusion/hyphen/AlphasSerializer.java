package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.UnsafeIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class AlphasSerializer {

	@org.junit.jupiter.api.Test
	void name() {
		Test data = new Test(false, true, false, false, true, true, false, true, false);
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
		System.out.println(data);
		System.out.println(test);
	}


	@Data
	public record Test(boolean b0, boolean b1, boolean b2, boolean b3, boolean b4, boolean b5, boolean b6, boolean b7,
					   @Nullable Boolean b8) {
	}
}
