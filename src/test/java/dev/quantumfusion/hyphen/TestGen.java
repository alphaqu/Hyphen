package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataNullable;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;

import java.util.Objects;

@Data
public class TestGen {
	public final String field;
	public final String field2;
	public final String field3;
	public final String field4;
	@DataNullable
	public final String field5;


	public TestGen(String field, String field2, String field3, String field4, String field5) {
		this.field = field;
		this.field2 = field2;
		this.field3 = field3;
		this.field4 = field4;
		this.field5 = field5;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TestGen testGen = (TestGen) o;
		return Objects.equals(field, testGen.field) && Objects.equals(field2, testGen.field2) && Objects.equals(field3, testGen.field3) && Objects.equals(field4, testGen.field4) && Objects.equals(field5, testGen.field5);
	}

	@Override
	public int hashCode() {
		return Objects.hash(field, field2, field3, field4, field5);
	}

	public static void main(String[] args) {
		var factory = SerializerFactory.createDebug(ByteBufferIO.class, TestGen.class);
		factory.addGlobalAnnotation("id", DataSubclasses.class, new Class[]{Integer.class, Float.class});
		factory.addGlobalAnnotation("id", Data.class, null);

		TestGen test = new TestGen("jklvdfa", "fsdaa", "vadhjklv", "fasd", "gfdsdafgsd");
		var serializer = factory.build();
		ByteBufferIO byteBufferIO = ByteBufferIO.create(serializer, test);
		serializer.put(byteBufferIO, test);
		byteBufferIO.rewind();
		TestGen out = serializer.get(byteBufferIO);
		System.out.println(out.equals(test));
	}


}
