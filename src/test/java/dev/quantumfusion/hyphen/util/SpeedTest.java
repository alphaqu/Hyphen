package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.SerializerFactory;
import dev.quantumfusion.hyphen.codegen.def.EnumDef;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.poly.general.EnumTest;

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

		for (long i = 0; i < 10_000; i++) {
			run(false, "cdSerializer", cdSerializer, byteBufferIO, 1000);
			run(false, "cdInvokeSerializer", cdInvokeSerializer, byteBufferIO, 1000);
			run(false, "valuesSerializer", valuesSerializer, byteBufferIO, 1000);
		}

		run(true, "cdSerializer", cdSerializer, byteBufferIO, ITERATION_COUNT);
		run(true, "cdInvokeSerializer", cdInvokeSerializer, byteBufferIO, ITERATION_COUNT);
		run(true, "valuesSerializer", valuesSerializer, byteBufferIO, ITERATION_COUNT);
		run(true, "cdInvokeSerializer", cdInvokeSerializer, byteBufferIO, ITERATION_COUNT);
		run(true, "valuesSerializer", valuesSerializer, byteBufferIO, ITERATION_COUNT);
		run(true, "cdSerializer", cdSerializer, byteBufferIO, ITERATION_COUNT);
	}

	private static void run(boolean print, String name, HyphenSerializer<ByteBufferIO, EnumTest> valuesSerializer, ByteBufferIO byteBufferIO, long count) {
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

		if (print)
			System.out.println("res " + name + "(" + res + "): " + (stopValues - startValues));
	}
}
