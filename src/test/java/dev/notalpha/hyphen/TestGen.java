package dev.notalpha.hyphen;

import dev.notalpha.hyphen.io.ByteBufferIO;
import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C3Def;

import java.lang.annotation.Annotation;


public record TestGen(C3Def<Integer>[] def) {

	public static void main(String[] args) {
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, TestGen.class);
		factory.addAnnotationProvider("id", new DataSubclasses() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return DataSubclasses.class;
			}

			@Override
			public Class<?>[] value() {
				return new Class[]{Integer.class, Float.class};
			}
		});
		TestGen test = null;
		var serializer = factory.build();
		ByteBufferIO byteBufferIO = ByteBufferIO.create(serializer, test);
		serializer.put(byteBufferIO, test);
		byteBufferIO.rewind();
		TestGen out = serializer.get(byteBufferIO);
		System.out.println(out.equals(test));
	}


}
