package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.io.ArrayIO;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.io.UnsafeIO;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IOTest {

	public abstract static class IOTestClass {
		static IOInterface ioInterface;
		static boolean booleanValue = false;
		static byte byteValue = Byte.MIN_VALUE;
		static char charValue = Character.MIN_VALUE;
		static short shortValue = Short.MIN_VALUE;
		static int intValue = Integer.MIN_VALUE;
		static long longValue = Long.MIN_VALUE;
		static float floatValue = Float.MIN_VALUE;
		static double doubleValue = Double.MIN_VALUE;
		static boolean[] booleanValueArray = new boolean[]{true, false, true, true, false};
		static byte[] byteValueArray = "gello there my name is alpha".getBytes(StandardCharsets.UTF_8);
		static char[] charValueArray = new char[]{Character.MAX_VALUE, 'a', 'l', 'p', 'h', 'a', Character.MIN_VALUE};
		static short[] shortValueArray = new short[]{Short.MAX_VALUE, 643, 124, Short.MIN_VALUE};
		static int[] intValueArray = new int[]{453254, 4312543, 54367, 3457, 3454563, 8456};
		static long[] longValueArray = new long[]{45354254, 431212543, 5423367, 345657, 345754563, 8453656};
		static float[] floatValueArray = new float[]{69.422430f, 69.44520f, 69.420234f};
		static double[] doubleValueArray = new double[]{69.426430f, 69.426430f, 69.4220f};

		@Nested
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		public class Read {
			@RepeatedTest(2)
			@Order(0)
			void getBoolean() {
				assertEquals(booleanValue, ioInterface.getBoolean());
			}

			@RepeatedTest(2)
			@Order(1)
			void getByte() {
				assertEquals(byteValue, ioInterface.getByte());
			}

			@RepeatedTest(2)
			@Order(2)
			void getChar() {
				assertEquals(charValue, ioInterface.getChar());
			}

			@RepeatedTest(2)
			@Order(3)
			void getShort() {
				assertEquals(shortValue, ioInterface.getShort());
			}

			@RepeatedTest(2)
			@Order(4)
			void getInt() {
				assertEquals(intValue, ioInterface.getInt());
			}

			@RepeatedTest(2)
			@Order(5)
			void getLong() {
				assertEquals(longValue, ioInterface.getLong());
			}

			@RepeatedTest(2)
			@Order(6)
			void getFloat() {
				assertEquals(floatValue, ioInterface.getFloat());
			}

			@RepeatedTest(2)
			@Order(7)
			void getDouble() {
				assertEquals(doubleValue, ioInterface.getDouble());
			}

			@RepeatedTest(2)
			@Order(8)
			void getBooleanArray() {
				assertArrayEquals(booleanValueArray, ioInterface.getBooleanArray(booleanValueArray.length));
			}

			@RepeatedTest(2)
			@Order(9)
			void getByteArray() {
				assertArrayEquals(byteValueArray, ioInterface.getByteArray(byteValueArray.length));
			}

			@RepeatedTest(2)
			@Order(10)
			void getCharArray() {
				assertArrayEquals(charValueArray, ioInterface.getCharArray(charValueArray.length));
			}

			@RepeatedTest(2)
			@Order(11)
			void getShortArray() {
				assertArrayEquals(shortValueArray, ioInterface.getShortArray(shortValueArray.length));
			}

			@RepeatedTest(2)
			@Order(12)
			void getIntArray() {
				assertArrayEquals(intValueArray, ioInterface.getIntArray(intValueArray.length));
			}

			@RepeatedTest(2)
			@Order(13)
			void getLongArray() {
				assertArrayEquals(longValueArray, ioInterface.getLongArray(longValueArray.length));
			}

			@RepeatedTest(2)
			@Order(14)
			void getFloatArray() {
				assertArrayEquals(floatValueArray, ioInterface.getFloatArray(floatValueArray.length));
			}

			@RepeatedTest(2)
			@Order(15)
			void getDoubleArray() {
				assertArrayEquals(doubleValueArray, ioInterface.getDoubleArray(doubleValueArray.length));
			}
		}

		@Nested
		public class Rewind {
			@RepeatedTest(2)
			@Order(0)
			void rewind() {
				ioInterface.rewind();
				assertEquals(6980000, ioInterface.getLong());
			}
		}

		@Nested
		@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
		public class Write {
			@RepeatedTest(2)
			@Order(0)
			void putBoolean() {
				ioInterface.putBoolean(booleanValue);
			}

			@RepeatedTest(2)
			@Order(1)
			void putByte() {
				ioInterface.putByte(byteValue);
			}

			@RepeatedTest(2)
			@Order(2)
			void putChar() {
				ioInterface.putChar(charValue);
			}

			@RepeatedTest(2)
			@Order(3)
			void putShort() {
				ioInterface.putShort(shortValue);
			}

			@RepeatedTest(2)
			@Order(4)
			void putInt() {
				ioInterface.putInt(intValue);
			}

			@RepeatedTest(2)
			@Order(5)
			void putLong() {
				ioInterface.putLong(longValue);
			}

			@RepeatedTest(2)
			@Order(6)
			void putFloat() {
				ioInterface.putFloat(floatValue);
			}

			@RepeatedTest(2)
			@Order(7)
			void putDouble() {
				ioInterface.putDouble(doubleValue);
			}

			@RepeatedTest(2)
			@Order(8)
			void putBooleanArray() {
				ioInterface.putBooleanArray(booleanValueArray);
			}

			@RepeatedTest(2)
			@Order(9)
			void putByteArray() {
				ioInterface.putByteArray(byteValueArray);
			}

			@RepeatedTest(2)
			@Order(10)
			void putCharArray() {
				ioInterface.putCharArray(charValueArray);
			}

			@RepeatedTest(2)
			@Order(11)
			void putShortArray() {
				ioInterface.putShortArray(shortValueArray);
			}

			@RepeatedTest(2)
			@Order(12)
			void putIntArray() {
				ioInterface.putIntArray(intValueArray);
			}

			@RepeatedTest(2)
			@Order(13)
			void putLongArray() {
				ioInterface.putLongArray(longValueArray);
			}

			@RepeatedTest(2)
			@Order(14)
			void putFloatArray() {
				ioInterface.putFloatArray(floatValueArray);
			}

			@RepeatedTest(2)
			@Order(15)
			void putDoubleArray() {
				ioInterface.putDoubleArray(doubleValueArray);
			}
		}

	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	public class UnsafeIOTest extends IOTestClass {

		@BeforeAll
		public void init() {
			ioInterface = UnsafeIO.create(2000);
			ioInterface.putLong(6980000);
		}
	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	public class HeapIOTest extends IOTestClass {

		@BeforeAll
		public void init() {
			ioInterface = ByteBufferIO.create(2000);
			ioInterface.putLong(6980000);
		}

	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	public class DirectIOTest extends IOTestClass {

		@BeforeAll
		public void init() {
			ioInterface = ByteBufferIO.createDirect(2000);
			ioInterface.putLong(6980000);
		}

	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	public class ArrayIOTest extends IOTestClass {

		@BeforeAll
		public void init() {
			ioInterface = ArrayIO.create(2000);
			ioInterface.putLong(6980000);
		}

	}
}
