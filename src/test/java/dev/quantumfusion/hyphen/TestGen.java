package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;

@Data
public class TestGen {
	@Data
	@DataSubclasses(Float.class)
	public Number field;


	public TestGen(@DataSubclasses(Float.class) Number field) {
		this.field = field;
	}

	public static void main(String[] args) {
		TestGen test = new TestGen(3f);
		final SerializerFactory<ByteBufferIO, TestGen> factory = SerializerFactory.createDebug(ByteBufferIO.class, TestGen.class);


		var serializer = factory.build();
		ByteBufferIO byteBufferIO = ByteBufferIO.create(serializer, test);
		serializer.put(byteBufferIO, test);
		byteBufferIO.rewind();
		TestGen out = serializer.get(byteBufferIO);
		System.out.println(out.equals(test));
	}


}
