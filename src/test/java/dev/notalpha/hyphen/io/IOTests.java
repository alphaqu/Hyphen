package dev.notalpha.hyphen.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.*;
import java.util.function.Function;

public class IOTests {
	private static final boolean[] BOOLEANS = {true, false, true, true, false, false};
	private static final byte[] BYTES = {0, Byte.MIN_VALUE, Byte.MAX_VALUE};
	private static final short[] SHORTS = {0, Short.MIN_VALUE, Short.MAX_VALUE};
	private static final char[] CHARS = {0, Character.MIN_VALUE, Character.MAX_VALUE, '\uD83E', '\uDD80'};
	private static final int[] INTS = {69, 0, Integer.MIN_VALUE, Integer.MAX_VALUE};
	private static final long[] LONGS = {0, Long.MIN_VALUE, Long.MAX_VALUE};
	private static final float[] FLOATS = {0, Float.MIN_VALUE, Float.MAX_VALUE, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NaN, Float.MIN_NORMAL};
	private static final double[] DOUBLES = {0, Double.MIN_VALUE, Double.MAX_VALUE, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN, Double.MIN_NORMAL};
	private static final String[] STRING = {"", "glosco", "ඞ", "Ayo, Crashnite?"};

	private static final int TEST_SIZE = 2000000;

	@Test
	void unsafe() {
		testIO(UnsafeIO::create);
	}

	@Test
	void unsafeWrapped() {
		testIO(size -> UnsafeIO.wrap(ByteBuffer.allocateDirect(size)));
	}

	@Test
	void directBytebuffer() {
		testIO(ByteBufferIO::createDirect);
		testIOByteBuffer(ByteBufferIO::create);
	}

	@Test
	void heapBytebuffer() {
		testIO(ByteBufferIO::create);
		testIOByteBuffer(ByteBufferIO::create);
	}


	@Test
	void array() {
		testIO(ArrayIO::create);
		testIOByteBuffer(ArrayIO::create);
	}

	private static <IO extends IOInterface> void testIO(Function<Integer, IO> ioCreator) {
		int stringSize = 0;
		for (String s : STRING) {
			stringSize += (s.length() * 2) + 4;
		}

		final IO io = ioCreator.apply(((((BOOLEANS.length) + (BYTES.length))) +
				(((SHORTS.length) + (CHARS.length)) * 2) +
				(((INTS.length) + (FLOATS.length)) * 4) +
				(((LONGS.length) + (DOUBLES.length)) * 8) + stringSize) * 2 * TEST_SIZE);
		// Primitives
		for (int i = 0; i < TEST_SIZE; i++) {
			for (var value : BOOLEANS) io.putBoolean(value);
			for (var value : BYTES) io.putByte(value);
			for (var value : SHORTS) io.putShort(value);
			for (var value : CHARS) io.putChar(value);
			for (var value : INTS) io.putInt(value);
			for (var value : FLOATS) io.putFloat(value);
			for (var value : LONGS) io.putLong(value);
			for (var value : DOUBLES) io.putDouble(value);
			for (var value : STRING) io.putString(value);
			io.putBooleanArray(BOOLEANS, BOOLEANS.length);
			io.putByteArray(BYTES, BYTES.length);
			io.putShortArray(SHORTS, SHORTS.length);
			io.putCharArray(CHARS, CHARS.length);
			io.putIntArray(INTS, INTS.length);
			io.putFloatArray(FLOATS, FLOATS.length);
			io.putLongArray(LONGS, LONGS.length);
			io.putDoubleArray(DOUBLES, DOUBLES.length);
			io.putStringArray(STRING, STRING.length);
		}

		io.rewind();
		for (int i = 0; i < TEST_SIZE; i++) {
			for (var value : BOOLEANS) Assertions.assertEquals(io.getBoolean(), value, "Boolean did not match");
			for (var value : BYTES) Assertions.assertEquals(io.getByte(), value, "Byte did not match");
			for (var value : SHORTS) Assertions.assertEquals(io.getShort(), value, "Short did not match");
			for (var value : CHARS) Assertions.assertEquals(io.getChar(), value, "Char did not match");
			for (var value : INTS) Assertions.assertEquals(io.getInt(), value, "Int did not match");
			for (var value : FLOATS) Assertions.assertEquals(io.getFloat(), value, "Float did not match");
			for (var value : LONGS) Assertions.assertEquals(io.getLong(), value, "Long did not match");
			for (var value : DOUBLES) Assertions.assertEquals(io.getDouble(), value, "Double did not match");
			for (var value : STRING) Assertions.assertEquals(io.getString(), value, "String did not match");
			Assertions.assertArrayEquals(io.getBooleanArray(BOOLEANS.length), BOOLEANS, "Boolean Array did not match");
			Assertions.assertArrayEquals(io.getByteArray(BYTES.length), BYTES, "Byte Array did not match");
			Assertions.assertArrayEquals(io.getShortArray(SHORTS.length), SHORTS, "Short Array did not match");
			Assertions.assertArrayEquals(io.getCharArray(CHARS.length), CHARS, "Char Array did not match");
			Assertions.assertArrayEquals(io.getIntArray(INTS.length), INTS, "Int Array did not match");
			Assertions.assertArrayEquals(io.getFloatArray(FLOATS.length), FLOATS, "Float Array did not match");
			Assertions.assertArrayEquals(io.getLongArray(LONGS.length), LONGS, "Long Array did not match");
			Assertions.assertArrayEquals(io.getDoubleArray(DOUBLES.length), DOUBLES, "Double Array did not match");
			Assertions.assertArrayEquals(io.getStringArray(STRING.length), STRING, "String Array did not match");
		}

		io.close();
	}


	private static <IO extends IOBufferInterface> void testIOByteBuffer(Function<Integer, IO> ioCreator) {
		var bytes = ByteBuffer.wrap(BYTES);
		var shorts = ShortBuffer.wrap(SHORTS);
		var chars = CharBuffer.wrap(CHARS);
		var ints = IntBuffer.wrap(INTS);
		var floats = FloatBuffer.wrap(FLOATS);
		var longs = LongBuffer.wrap(LONGS);
		var doubles = DoubleBuffer.wrap(DOUBLES);

		int entriesSize = (((BYTES.length))) +
				(((SHORTS.length) + (CHARS.length)) * 2) +
				(((INTS.length) + (FLOATS.length)) * 4) +
				(((LONGS.length) + (DOUBLES.length)) * 8);

		final IO io = ioCreator.apply((entriesSize) * TEST_SIZE);
		// Primitives
		for (int i = 0; i < TEST_SIZE; i++) {
			io.putByteBuffer(bytes, BYTES.length);
			io.putShortBuffer(shorts, SHORTS.length);
			io.putCharBuffer(chars, CHARS.length);
			io.putIntBuffer(ints, INTS.length);
			io.putFloatBuffer(floats, FLOATS.length);
			io.putLongBuffer(longs, LONGS.length);
			io.putDoubleBuffer(doubles, DOUBLES.length);
			bytes.rewind();
			shorts.rewind();
			chars.rewind();
			ints.rewind();
			floats.rewind();
			longs.rewind();
			doubles.rewind();
		}

		io.rewind();
		for (int i = 0; i < TEST_SIZE; i++) {
			{
				var buffer = ByteBuffer.allocate(BYTES.length);
				io.getByteBuffer(buffer, BYTES.length);
				Assertions.assertArrayEquals(buffer.array(), BYTES, "Byte Array did not match");
				Assertions.assertEquals(buffer.position(), BYTES.length);
			}
			{
				var buffer = ShortBuffer.allocate(SHORTS.length);
				io.getShortBuffer(buffer, SHORTS.length);
				Assertions.assertArrayEquals(buffer.array(), SHORTS, "Short Array did not match");
				Assertions.assertEquals(buffer.position(), SHORTS.length);
			}
			{
				var buffer = CharBuffer.allocate(CHARS.length);
				io.getCharBuffer(buffer, CHARS.length);
				Assertions.assertArrayEquals(buffer.array(), CHARS, "Char Array did not match");
				Assertions.assertEquals(buffer.position(), CHARS.length);
			}
			{
				var buffer = IntBuffer.allocate(INTS.length);
				io.getIntBuffer(buffer, INTS.length);
				Assertions.assertArrayEquals(buffer.array(), INTS, "Int Array did not match");
				Assertions.assertEquals(buffer.position(), INTS.length);
			}
			{
				var buffer = FloatBuffer.allocate(FLOATS.length);
				io.getFloatBuffer(buffer, FLOATS.length);
				Assertions.assertArrayEquals(buffer.array(), FLOATS, "Float Array did not match");
				Assertions.assertEquals(buffer.position(), FLOATS.length);
			}
			{
				var buffer = LongBuffer.allocate(LONGS.length);
				io.getLongBuffer(buffer, LONGS.length);
				Assertions.assertArrayEquals(buffer.array(), LONGS, "Long Array did not match");
				Assertions.assertEquals(buffer.position(), LONGS.length);
			}
			{
				var buffer = DoubleBuffer.allocate(DOUBLES.length);
				io.getDoubleBuffer(buffer, DOUBLES.length);
				Assertions.assertArrayEquals(buffer.array(), DOUBLES, "Double Array did not match");
				Assertions.assertEquals(buffer.position(), DOUBLES.length);
			}
		}

		io.close();
	}
}
