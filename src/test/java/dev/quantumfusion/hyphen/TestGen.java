package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataGlobalAnnotation;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;

@Data
public class TestGen {
	@Data
	public Test field;


	public TestGen(Test field) {
		this.field = field;
	}


	public static class Test {
		@DataGlobalAnnotation("things")
		public Number testing;

		public Test(Number testing) {
			this.testing = testing;
		}
	}



	public static void main(String[] args) {
		TestGen test = new TestGen(new Test( 69f));
		final SerializerFactory<ByteBufferIO, TestGen> factory = SerializerFactory.createDebug(ByteBufferIO.class, TestGen.class);
		factory.addGlobalAnnotation("things", DataSubclasses.class, new Class[]{Integer.class, Float.class});
		factory.addGlobalAnnotation("things", Data.class, null);

		var serializer = factory.build();
		ByteBufferIO byteBufferIO = ByteBufferIO.create(serializer, test);
		serializer.put(byteBufferIO, test);
		byteBufferIO.rewind();
		TestGen out = serializer.get(byteBufferIO);
		System.out.println(out.equals(test));
	}


}
