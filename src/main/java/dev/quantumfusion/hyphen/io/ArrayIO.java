package dev.quantumfusion.hyphen.io;

import dev.quantumfusion.hyphen.HyphenSerializer;

import java.nio.charset.StandardCharsets;

/**
 * ArrayIO. An IO that internally uses Array.
 */
@SuppressWarnings({"FinalMethodInFinalClass", "FinalStaticMethod", "unused"})
public final class ArrayIO implements IOInterface {
	private final byte[] bytes;
	private int pos;

	private ArrayIO(final byte[] bytes, final int pos) {
		this.bytes = bytes;
		this.pos = pos;
	}

	public static final ArrayIO create(final int size) {
		return new ArrayIO(new byte[size], 0);
	}

	public static final <O> ArrayIO create(final HyphenSerializer<ArrayIO, O> serializer, final O data) {
		return create(serializer.measure(data));
	}


	// ======================================= FUNC ======================================= //
	@Override
	public final void rewind() {
		pos = 0;
	}


	@Override
	public final int pos() {
		return pos;
	}

	@Override
	public final void close() {
		// Hey jvm. GC the array. thx.
	}


	// ======================================== GET ======================================== //
	@Override
	public final boolean getBoolean() {
		return getByte() != 0;
	}


	@Override
	public final byte getByte() {
		return bytes[pos++];
	}

	@Override
	public final char getChar() {
		final char c = (char) (bytes[pos] & 0xFF | (bytes[pos + 1] & 0xFF) << 8);
		pos += 2;
		return c;
	}


	@Override
	public final short getShort() {
		final short c = (short) (bytes[pos] & 0xFF | (bytes[pos + 1] & 0xFF) << 8);
		pos += 2;
		return c;
	}


	@Override
	public final int getInt() {
		final int result = bytes[pos] & 0xFF | (bytes[pos + 1] & 0xFF) << 8 | (bytes[pos + 2] & 0xFF) << 16 | (bytes[pos + 3] & 0xFF) << 24;
		pos += 4;
		return result;
	}


	@Override
	public final long getLong() {
		final long result =
				(bytes[pos] & 0xFF | (bytes[pos + 1] & 0xFF) << 8)
						| (bytes[pos + 2] & 0xFF) << 16 | (long) (bytes[pos + 3] & 0xFF) << 24
						| (long) (bytes[pos + 4] & 0xFF) << 32 | (long) (bytes[pos + 5] & 0xFF) << 40
						| (long) (bytes[pos + 6] & 0xFF) << 48 | (long) (bytes[pos + 7] & 0xFF) << 56;
		pos += 8;
		return result;
	}


	@Override
	public final float getFloat() {
		return Float.intBitsToFloat(getInt());
	}


	@Override
	public final double getDouble() {
		return Double.longBitsToDouble(getLong());
	}


	@Override
	public final String getString() {
		final int length = getInt();
		final byte[] byteArray = getByteArray(length);
		return new String(byteArray, 0, length, StandardCharsets.UTF_8);
	}


	// ======================================== PUT ======================================== //
	@Override
	public final void putBoolean(final boolean value) {
		putByte((byte) (value ? 1 : 0));
	}


	@Override
	public final void putByte(final byte value) {
		bytes[pos++] = value;
	}


	@Override
	public final void putChar(final char value) {
		bytes[pos] = (byte) value;
		bytes[pos + 1] = (byte) (value >>> 8);
		pos += 2;
	}


	@Override
	public final void putShort(final short value) {
		bytes[pos] = (byte) value;
		bytes[pos + 1] = (byte) (value >>> 8);
		pos += 2;
	}


	@Override
	public final void putInt(final int value) {
		bytes[pos] = (byte) value;
		bytes[pos + 1] = (byte) (value >>> 8);
		bytes[pos + 2] = (byte) (value >>> 16);
		bytes[pos + 3] = (byte) (value >>> 24);
		pos += 4;
	}


	@Override
	public final void putLong(final long value) {
		final int low = (int) value;
		final int high = (int) (value >>> 32);
		bytes[pos] = (byte) low;
		bytes[pos + 1] = (byte) (low >>> 8);
		bytes[pos + 2] = (byte) (low >>> 16);
		bytes[pos + 3] = (byte) (low >>> 24);
		bytes[pos + 4] = (byte) high;
		bytes[pos + 5] = (byte) (high >>> 8);
		bytes[pos + 6] = (byte) (high >>> 16);
		bytes[pos + 7] = (byte) (high >>> 24);
		pos += 8;
	}


	@Override
	public final void putFloat(final float value) {
		putInt(Float.floatToIntBits(value));
	}


	@Override
	public final void putDouble(final double value) {
		putLong(Double.doubleToLongBits(value));
	}


	@Override
	public final void putString(final String value) {
		final byte[] array = value.getBytes(StandardCharsets.UTF_8);
		final int length = array.length;
		putInt(length);
		putByteArray(array, length);
	}


	// ====================================== GET_ARR ======================================== //
	@Override
	public final boolean[] getBooleanArray(final int length) {
		final boolean[] out = new boolean[length];
		for (int i = 0; i < length; i++)
			out[i] = getByte() == 1;
		return out;
	}


	@Override
	public final byte[] getByteArray(final int length) {
		final byte[] out = new byte[length];
		for (int i = 0; i < length; i++)
			out[i] = getByte();
		return out;
	}


	@Override
	public final char[] getCharArray(final int length) {
		final char[] out = new char[length];
		for (int i = 0; i < length; i++)
			out[i] = getChar();
		return out;
	}


	@Override
	public final short[] getShortArray(final int length) {
		final short[] out = new short[length];
		for (int i = 0; i < length; i++)
			out[i] = getShort();
		return out;
	}


	@Override
	public final int[] getIntArray(final int length) {
		final int[] out = new int[length];
		for (int i = 0; i < length; i++)
			out[i] = getInt();
		return out;
	}


	@Override
	public final long[] getLongArray(final int length) {
		final long[] out = new long[length];
		for (int i = 0; i < length; i++)
			out[i] = getLong();
		return out;
	}


	@Override
	public final float[] getFloatArray(final int length) {
		final float[] out = new float[length];
		for (int i = 0; i < length; i++)
			out[i] = getFloat();
		return out;
	}


	@Override
	public final double[] getDoubleArray(final int length) {
		final double[] out = new double[length];
		for (int i = 0; i < length; i++)
			out[i] = getDouble();
		return out;
	}


	@Override
	public final String[] getStringArray(final int length) {
		final String[] out = new String[length];
		for (int i = 0; i < length; i++)
			out[i] = getString();
		return out;
	}


	// ====================================== PUT_ARR ======================================== //
	@Override
	public final void putBooleanArray(final boolean[] value, final int length) {
		for (int i = 0; i < length; i++) putByte((byte) (value[i] ? 1 : 0));
	}


	@Override
	public final void putByteArray(final byte[] value, final int length) {
		for (int i = 0; i < length; i++) putByte(value[i]);
	}


	@Override
	public final void putCharArray(final char[] value, final int length) {
		for (int i = 0; i < length; i++) putChar(value[i]);
	}


	@Override
	public final void putShortArray(final short[] value, final int length) {
		for (int i = 0; i < length; i++) putShort(value[i]);
	}


	@Override
	public final void putIntArray(final int[] value, final int length) {
		for (int i = 0; i < length; i++) putInt(value[i]);
	}


	@Override
	public final void putLongArray(final long[] value, final int length) {
		for (int i = 0; i < length; i++) putLong(value[i]);
	}


	@Override
	public final void putFloatArray(final float[] value, final int length) {
		for (int i = 0; i < length; i++) putFloat(value[i]);
	}


	@Override
	public final void putDoubleArray(final double[] value, final int length) {
		for (int i = 0; i < length; i++) putDouble(value[i]);
	}


	@Override
	public final void putStringArray(final String[] value, final int length) {
		for (int i = 0; i < length; i++) putString(value[i]);
	}
}
