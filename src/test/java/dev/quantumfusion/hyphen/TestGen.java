package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;

@Data
public class TestGen extends Test2<Double> {
	@Data
	public final String field;

	public TestGen(Double o, String field) {
		super(o);
		this.field = field;
	}


	public static void main(String[] args) {
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, TestGen.class);
		factory.addGlobalAnnotation("id", DataSubclasses.class, new Class[]{Integer.class, Float.class});
		factory.addGlobalAnnotation("id", Data.class, null);

		TestGen test = new TestGen(432.5, "4321");
		var serializer = factory.build();
		ByteBufferIO byteBufferIO = ByteBufferIO.create(serializer, test);
		serializer.put(byteBufferIO, test);
		byteBufferIO.rewind();
		TestGen out = serializer.get(byteBufferIO);
		System.out.println(out.equals(test));
	}


}
