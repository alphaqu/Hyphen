package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.poly.general.DoubleC;

import static dev.quantumfusion.hyphen.Options.FAST_ALLOC;

public class ProduceSerializer {
	public static void main(String[] args) {
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, DoubleC.class);
		factory.setOption(FAST_ALLOC, false);
		factory.build();
	}
}
