package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.def.EnumDef;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import dev.quantumfusion.hyphen.scan.poly.enums.EnumNullableNullTest;

import static dev.quantumfusion.hyphen.Options.*;

public class ProduceSerializer {
	public static void main(String[] args) {
		EnumDef.USE_CONSTANT_DYNAMIC = false;
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, EnumNullableNullTest.class);
		factory.setOption(FAST_ALLOC, false);
		factory.setOption(DISABLE_GET, true);
		factory.setOption(DISABLE_PUT, true);
		factory.build();
	}

	@Data
	static class Hi{
		@DataNullable
		public Integer i;
	}
}
