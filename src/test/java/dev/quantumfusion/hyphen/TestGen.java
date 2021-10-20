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
		@DataGlobalAnnotation("id")
		public Number testing;

		public Test(Number testing) {
			this.testing = testing;
		}
	}



	public static void main(String[] args) {
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, TestGen.class);
		factory.addGlobalAnnotation("id", DataSubclasses.class, new Class[]{Integer.class, Float.class});
		factory.addGlobalAnnotation("id", Data.class, null);

		TestGen test = new TestGen(new Test( 69f));
		var serializer = factory.build();
		ByteBufferIO byteBufferIO = ByteBufferIO.create(serializer, test);
		serializer.put(byteBufferIO, test);
		byteBufferIO.rewind();
		TestGen out = serializer.get(byteBufferIO);
		System.out.println(out.equals(test));
	}


}
