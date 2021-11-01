package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlphasSerializer {

	@org.junit.jupiter.api.Test
	void name() {
		FuckYouGlisco data = new FuckYouGlisco(new int[]{2345});

		DashShader shader = new DashShader(Map.of("fdjaslkjflasd", 423342, "fasdioufaso", 423234), "fldksaj", new ArrayList<>(), data,
										   data, data, null, data, data, data, null, data, List.of("fsdjalk"));
		test(shader);
	}

	public static <O> void test(O data) {
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, (Class<O>) data.getClass());
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
		public final Map<String, Integer> samplers;
		public final String name;
		public final List<String> attributeNames;
		@DataNullable
		public final FuckYouGlisco modelViewMat;
		@DataNullable
		public final FuckYouGlisco projectionMat;
		@DataNullable
		public final FuckYouGlisco textureMat;
		@DataNullable
		public final FuckYouGlisco screenSize;
		@DataNullable
		public final FuckYouGlisco colorModulator;
		@DataNullable
		public final FuckYouGlisco light0Direction;
		@DataNullable
		public final FuckYouGlisco light1Direction;
		@DataNullable
		public final FuckYouGlisco fogStart;
		@DataNullable
		public final FuckYouGlisco fogStart2;
		public final List<String> samplerNames;


		transient FuckYouGlisco toApply;


		public DashShader(Map<String, Integer> samplers,
				String name,
				List<String> attributeNames,
				FuckYouGlisco modelViewMat,
				FuckYouGlisco projectionMat,
				FuckYouGlisco textureMat,
				FuckYouGlisco screenSize,
				FuckYouGlisco colorModulator,
				FuckYouGlisco light0Direction,
				FuckYouGlisco light1Direction,
				FuckYouGlisco fogStart,
				FuckYouGlisco fogStart2, List<String> samplerNames) {
			this.samplers = samplers;
			this.name = name;
			this.attributeNames = attributeNames;
			this.modelViewMat = modelViewMat;
			this.projectionMat = projectionMat;
			this.textureMat = textureMat;
			this.screenSize = screenSize;
			this.colorModulator = colorModulator;
			this.light0Direction = light0Direction;
			this.light1Direction = light1Direction;
			this.fogStart = fogStart;
			this.fogStart2 = fogStart2;
			this.samplerNames = samplerNames;
		}
	}

	@Data
	public static class FuckYouGlisco {
		public final int[] count;

		public FuckYouGlisco(int[] count) {
			this.count = count;
		}
	}
}
