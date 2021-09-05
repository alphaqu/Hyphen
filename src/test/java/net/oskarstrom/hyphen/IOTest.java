package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.io.ByteBufferIO;
import net.oskarstrom.hyphen.io.IOInterface;
import net.oskarstrom.hyphen.io.UnsafeIO;
import org.junit.jupiter.api.*;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class IOTest {

	public abstract static class IOTestClass {
		IOInterface ioInterface;

		@BeforeEach
		public void prepare() {
			ioInterface.rewind();
		}

		@Nested
		public class getBooleanTest {
			@Test
			public void test() {
				ioInterface.putBoolean(true);
				ioInterface.rewind();
				assertTrue(ioInterface.getBoolean());
			}
		}

		@Nested
		public class getBooleanArrayTest {
			@Test
			public void test() {
				boolean[] value = {true,true, false, true};
				ioInterface.putBooleanArray(value);
				ioInterface.rewind();
				assertArrayEquals(value, ioInterface.getBooleanArray(value.length));
			}
		}

		@Nested
		public class getByteTest {
			@Test
			public void test() {
				var value = (byte) 69;
				ioInterface.putByte(value);
				ioInterface.rewind();
				assertEquals(value, ioInterface.getByte());

			}
		}

		@Nested
		public class getByteArrayTest {
			@Test
			public void test() {
				byte[] value = {69, 69};
				ioInterface.putByteArray(value);
				ioInterface.rewind();
				assertArrayEquals(value, ioInterface.getByteArray(value.length));

			}
		}

		@Nested
		public class getCharTest {
			@Test
			public void test() {
				var value = 'f';
				ioInterface.putChar(value);
				ioInterface.rewind();
				assertEquals(value, ioInterface.getChar());

			}
		}

		@Nested
		public class getCharArrayTest {
			@Test
			public void test() {
				char[] value = {'n', 'i', 'c', 'e'};
				ioInterface.putCharArray(value);
				ioInterface.rewind();
				assertArrayEquals(value, ioInterface.getCharArray(value.length));

			}
		}

		@Nested
		public class getShortTest {
			@Test
			public void test() {
				var value = (short) 69;
				ioInterface.putShort(value);
				ioInterface.rewind();
				assertEquals(value, ioInterface.getShort());

			}
		}

		@Nested
		public class getShortArrayTest {
			@Test
			public void test() {
				short[] value = {420, 69};
				ioInterface.putShortArray(value);
				ioInterface.rewind();
				assertArrayEquals(value, ioInterface.getShortArray(value.length));
			}
		}

		@Nested
		public class getIntTest {
			@Test
			public void test() {
				var value = 69;
				ioInterface.putInt(value);
				ioInterface.rewind();
				assertEquals(value, ioInterface.getInt());

			}
		}

		@Nested
		public class getIntArrayTest {
			@Test
			public void test() {
				int[] value = {420, 69};
				ioInterface.putIntArray(value);
				ioInterface.rewind();
				assertArrayEquals(value, ioInterface.getIntArray(value.length));
			}
		}

		@Nested
		public class getLongTest {
			@Test
			public void test() {
				var value = 96969696969696969L;
				ioInterface.putLong(value);
				ioInterface.rewind();
				assertEquals(value, ioInterface.getLong());

			}
		}

		@Nested
		public class getLongArrayTest {
			@Test
			public void test() {
				long[] value = {420, 69};
				ioInterface.putLongArray(value);
				ioInterface.rewind();
				assertArrayEquals(value, ioInterface.getLongArray(value.length));
			}
		}

		@Nested
		public class getFloatTest {
			@Test
			public void test() {
				var value = 69.420f;
				ioInterface.putFloat(value);
				ioInterface.rewind();
				assertEquals(value, ioInterface.getFloat());

			}
		}

		@Nested
		public class getFloatArrayTest {
			@Test
			public void test() {
				float[] value = {420.69f, 69.420f};
				ioInterface.putFloatArray(value);
				ioInterface.rewind();
				assertArrayEquals(value, ioInterface.getFloatArray(value.length));
			}
		}

		@Nested
		public class getDoubleTest {
			@Test
			public void test() {
				var value = 69.420420420420420420420420420D;
				ioInterface.putDouble(value);
				ioInterface.rewind();
				assertEquals(value, ioInterface.getDouble());
			}
		}

		@Nested
		public class getDoubleArrayTest {
			@Test
			public void test() {
				double[] value = {420.69f, 69.420f};
				ioInterface.putDoubleArray(value);
				ioInterface.rewind();
				assertArrayEquals(value, ioInterface.getDoubleArray(value.length));
			}
		}
	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	public class UnsafeIOTest extends IOTestClass {

		@BeforeAll
		public void init() {
			ioInterface = UnsafeIO.create(16);
		}
	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	public class HeapIOTest extends IOTestClass {

		@BeforeAll
		public void init() {
			ioInterface = ByteBufferIO.create(16);
		}

	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	public class DirectIOTest extends IOTestClass {

		@BeforeAll
		public void init() {
			ioInterface = ByteBufferIO.createDirect(16);
		}

	}
}
