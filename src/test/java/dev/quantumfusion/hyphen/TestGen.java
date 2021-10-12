package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.io.IOInterface;

public class TestGen {

	public static void main(String[] args) {
		var factory = SerializerFactory.create(ByteBufferIO.class, Test.class, "things", true);
		final HyphenSerializer<IOInterface, Test> build = factory.build();
		build.get(ByteBufferIO.create(10000));
	}

	public static class Test {
		@Serialize
		public int anInt;

		public Test(int anInt) {
			this.anInt = anInt;
		}
	}
}
