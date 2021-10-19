package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;

import java.util.Arrays;
import java.util.Objects;

@Data
public class TestGen {
	@Data
	@DataSubclasses(Float.class)
	public Number[] field;


	public TestGen(@DataSubclasses(Float.class) Number[] field) {
		this.field = field;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TestGen testGen = (TestGen) o;
		return Arrays.equals(field, testGen.field);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(field);
	}

	public static void main(String[] args) {
		TestGen test = new TestGen( new Number[]{4f,2f,7f,5f});
		final SerializerFactory<ByteBufferIO, TestGen> factory = SerializerFactory.createDebug(ByteBufferIO.class, TestGen.class);


		var serializer = factory.build();
		ByteBufferIO byteBufferIO = ByteBufferIO.create(serializer, test);
		serializer.put(byteBufferIO, test);
		byteBufferIO.rewind();
		TestGen out = serializer.get(byteBufferIO);
		System.out.println(out.equals(test));
	}


}
