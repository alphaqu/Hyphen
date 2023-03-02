package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C3Def;


public record TestGen(C3Def<Integer>[] def) {

	public static void main(String[] args) {
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, TestGen.class);
		factory.addAnnotationProvider("id", DataSubclasses.class, new Class[]{Integer.class, Float.class});

		TestGen test = null;
		var serializer = factory.build();
		ByteBufferIO byteBufferIO = ByteBufferIO.create(serializer, test);
		serializer.put(byteBufferIO, test);
		byteBufferIO.rewind();
		TestGen out = serializer.get(byteBufferIO);
		System.out.println(out.equals(test));
	}


}
