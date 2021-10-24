package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.def.EnumDef;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.simple.MapTest;

import static dev.quantumfusion.hyphen.Options.FAST_ALLOC;

public class ProduceSerializer {
	public static void main(String[] args) {
		EnumDef.USE_CONSTANT_DYNAMIC = false;
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, MapTest.class);
		factory.setOption(FAST_ALLOC, false);
		factory.build();
	}
}
