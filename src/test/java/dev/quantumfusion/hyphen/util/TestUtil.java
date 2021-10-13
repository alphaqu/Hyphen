package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.SerializerFactory;
import dev.quantumfusion.hyphen.io.ByteBufferIO;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;


public class TestUtil {


	public static <O> void test(Class<O> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		if (clazz.getDeclaredAnnotation(TestThis.class) != null) {
			// generation
			var factory = SerializerFactory.create(ByteBufferIO.class, clazz);
			var serializer = factory.build();

			var expectedScan = clazz.getDeclaredAnnotation(ExpectedResult.class);
			if (expectedScan != null) {
				// TODO add fingerprint methods that check if scan was correct
				var expected = expectedScan.value();
			}

			// create data instance
			var data = clazz.getConstructor().newInstance();

			// measure
			var measuredSize = serializer.measure(data);
			var io = ByteBufferIO.create(measuredSize * 2);

			// put
			serializer.put(io, data);

			// measure check
			final int actualSize = io.pos();
			if (actualSize > measuredSize)
				fail("Actual size is bigger than measured size. " + actualSize + " > " + measuredSize);
			else if (measuredSize > actualSize)
				fail("Measured size is bigger than actual size. " + measuredSize + " > " + actualSize);

			// rewind to 0
			io.rewind();

			// get
			var dataOut = serializer.get(io);

			// result check
			if (!data.equals(dataOut))
				fail("Objects do not match\n" + data + "\n != \n" + dataOut);
		}
	}
}
