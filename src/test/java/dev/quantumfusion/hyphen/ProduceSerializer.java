package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.def.EnumDef;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import dev.quantumfusion.hyphen.scan.simple.map.MapIITest;

import java.io.IOException;

import static dev.quantumfusion.hyphen.Options.FAST_ALLOC;

public class ProduceSerializer {
	public static void main(String[] args) throws IOException {
		EnumDef.USE_CONSTANT_DYNAMIC = false;
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, MapIITest.class);
		factory.setOption(FAST_ALLOC, false);
		// factory.setOption(DISABLE_GET, true);
		// factory.setOption(DISABLE_PUT, true);
		// factory.setOption(DISABLE_MEASURE, true);
		factory.build();

		Runtime.getRuntime().exec("java -jar K:/IdeaProjects/quiltflower/build/libs/quiltflower-1.6.0+local.jar HyphenSerializer.class .ignore/");
	}

	@Data
	static class Hi {
		@DataNullable
		public Integer i;
	}
}
