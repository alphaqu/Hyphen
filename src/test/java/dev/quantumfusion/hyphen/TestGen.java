package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;

@Data
public class TestGen {
	@Data
	public Test field;


	public TestGen(Test field) {
		this.field = field;
	}


	public static class Test {
		@Data
		public Test2 testing;
	}

	public static class Test2<K> {
		@Data
		public K test;
	}

	public static void main(String[] args) {
		TestGen test = new TestGen(new Test());
		final SerializerFactory<ByteBufferIO, TestGen> factory = SerializerFactory.createDebug(ByteBufferIO.class, TestGen.class);


		var serializer = factory.build();
		ByteBufferIO byteBufferIO = ByteBufferIO.create(serializer, test);
		serializer.put(byteBufferIO, test);
		byteBufferIO.rewind();
		TestGen out = serializer.get(byteBufferIO);
		System.out.println(out.equals(test));
	}


}
