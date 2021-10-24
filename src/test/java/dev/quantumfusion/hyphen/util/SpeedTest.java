package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.SerializerFactory;
import dev.quantumfusion.hyphen.codegen.def.EnumDef;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.poly.enums.EnumTest;

import static dev.quantumfusion.hyphen.Options.FAST_ALLOC;

public class SpeedTest {
	private static <T> HyphenSerializer<ByteBufferIO, T> getSerializer(Class<T> cls) {
		var factory = SerializerFactory.create(ByteBufferIO.class, cls);
		factory.setOption(FAST_ALLOC, true);
		return factory.build();
	}

	private static final long ITERATION_COUNT = 1_000_000_000L;

	public static void main(String[] args) {
		EnumDef.USE_CONSTANT_DYNAMIC = true;
		EnumDef.USE_CONSTANT_DYNAMIC_INVOKE = true;
		var cdInvokeSerializer = getSerializer(EnumTest.class);
		EnumDef.USE_CONSTANT_DYNAMIC_INVOKE = false;
		var cdSerializer = getSerializer(EnumTest.class);
		EnumDef.USE_CONSTANT_DYNAMIC = false;
		var valuesSerializer = getSerializer(EnumTest.class);

		ByteBufferIO byteBufferIO = ByteBufferIO.create(1);
		byteBufferIO.putByte((byte) ('R' - 'A'));


		byteBufferIO.rewind();
		System.out.println(cdSerializer.get(byteBufferIO));

		for (long i = 0; i < 1000000; i++) {
			run(false, "cdSerializer", cdSerializer, byteBufferIO, 1000);
			run(false, "cdInvokeSerializer", cdInvokeSerializer, byteBufferIO, 1000);
			run(false, "valuesSerializer", valuesSerializer, byteBufferIO, 1000);
		}

		int valuesSerializerTime = 0;
		int cdInvokeSerializerTime = 0;
		int cdSerializerTime = 0;
		for (int i = 0; i < 10; i++) {
			valuesSerializerTime += run(true, "valuesSerializer", valuesSerializer, byteBufferIO, ITERATION_COUNT);
			cdInvokeSerializerTime += run(true, "cdInvokeSerializer", cdInvokeSerializer, byteBufferIO, ITERATION_COUNT);
			cdSerializerTime += run(true, "cdSerializer", cdSerializer, byteBufferIO, ITERATION_COUNT);
			System.out.println(i);
		}

		System.out.println("Values: " + valuesSerializerTime + "ms  Invoke: " + cdInvokeSerializerTime + "ms  Cd: " + cdSerializerTime + "ms");
	}

	private static long run(boolean print, String name, HyphenSerializer<ByteBufferIO, EnumTest> valuesSerializer, ByteBufferIO byteBufferIO, long count) {
		long startValues = System.currentTimeMillis();

		for (long i = 0; i < count; i++) {
			byteBufferIO.rewind();
			valuesSerializer.get(byteBufferIO);
		}

		int res = 0;
		for (long i = 0; i < count; i++) {
			byteBufferIO.rewind();
			res += valuesSerializer.get(byteBufferIO).hashCode();
		}

		long stopValues = System.currentTimeMillis();

		return (stopValues - startValues);
	}
}
