package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;

@Data
public record TestGen(@DataNullable String field, @DataNullable String field2, @DataNullable String field3,
					  @DataNullable String field4, @DataNullable String field5, boolean b1, boolean b2) {

	public static void main(String[] args) {
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, TestGen.class);
		factory.addGlobalAnnotation("id", DataSubclasses.class, new Class[]{Integer.class, Float.class});
		factory.addGlobalAnnotation("id", Data.class, null);

		TestGen test = new TestGen("jklvdfa", "fsdaa", "vadhjklv", "fasd", "gfdsdafgsd", false, true);
		var serializer = factory.build();
		ByteBufferIO byteBufferIO = ByteBufferIO.create(serializer, test);
		serializer.put(byteBufferIO, test);
		byteBufferIO.rewind();
		TestGen out = serializer.get(byteBufferIO);
		System.out.println(out.equals(test));
	}


}
