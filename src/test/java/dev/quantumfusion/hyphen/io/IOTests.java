package dev.quantumfusion.hyphen.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.function.Function;

public class IOTests {
	private static final boolean[] BOOLEANS = {true, false, true, true, false, false};
	private static final byte[] BYTES = {0, Byte.MIN_VALUE, Byte.MAX_VALUE};
	private static final short[] SHORTS = {0, Short.MIN_VALUE, Short.MAX_VALUE};
	private static final char[] CHARS = {0, Character.MIN_VALUE, Character.MAX_VALUE, '\uD83E', '\uDD80'};
	private static final int[] INTS = {0, Integer.MIN_VALUE, Integer.MAX_VALUE};
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
	}

	@Test
	void heapBytebuffer() {
		testIO(ByteBufferIO::create);
	}

	@Test
	void array() {
		testIO(ArrayIO::create);
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
}
