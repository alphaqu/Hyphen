package dev.notalpha.hyphen;

import dev.notalpha.hyphen.codegen.def.EnumDef;
import dev.notalpha.hyphen.io.ByteBufferIO;
import dev.notalpha.hyphen.scan.annotations.DataNullable;
import dev.notalpha.hyphen.test.simple.ExtendTest;

import java.io.IOException;
import java.nio.file.Path;

public class ProduceSerializer {
	public static void main(String[] args) throws IOException {
		EnumDef.USE_CONSTANT_DYNAMIC = false;
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, ExtendTest.class);
		factory.setOption(Options.FAST_ALLOC, false);
		factory.setExportDir(Path.of("./"));
		// factory.setOption(DISABLE_GET, true);
		// factory.setOption(DISABLE_PUT, true);
		// factory.setOption(DISABLE_MEASURE, true);
		factory.build().measure(null);

	//	Runtime.getRuntime().exec("java -jar K:/IdeaProjects/quiltflower/build/libs/quiltflower-1.6.0+local.jar HyphenSerializer.class .ignore/");
	}

		public static class Hi {
		public final int count;
		public final int dataType;
		@DataNullable
		public final int[] intData;
		@DataNullable
		public final float[] floatData;
		public final String name;


		public Hi(int count, int dataType, int[] intData, float[] floatData, String name) {
			this.count = count;
			this.dataType = dataType;
			this.intData = intData;
			this.floatData = floatData;
			this.name = name;
		}

	}


}
