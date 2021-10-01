package dev.quantumfusion.hyphen.io;

import java.nio.charset.StandardCharsets;

@SuppressWarnings({"FinalMethodInFinalClass", "FinalStaticMethod"})
public final class ArrayIO {
	private final byte[] bytes;
	private int pos;

	private ArrayIO(final byte[] bytes, final int pos) {
		this.bytes = bytes;
		this.pos = pos;
	}

	public static final ArrayIO create(final int size) {
		return new ArrayIO(new byte[size], 0);
	}


	// ======================================= FUNC ======================================= //
	public final void rewind() {
		pos = 0;
	}


	public final int pos() {
		return pos;
	}

	public final void close() {
		// Hey jvm. GC the array. thx.
	}


	// ======================================== GET ======================================== //
	public final boolean getBoolean() {
		return getByte() != 0;
	}


	public final byte getByte() {
		return bytes[pos++];
	}

	public final char getChar() {
		final char c = (char) (bytes[pos] & 0xFF | (bytes[pos + 1] & 0xFF) << 8);
		pos += 2;
		return c;
	}


	public final short getShort() {
		final short c = (short) (bytes[pos] & 0xFF | (bytes[pos + 1] & 0xFF) << 8);
		pos += 2;
		return c;
	}


	public final int getInt() {
		final int result = bytes[pos] & 0xFF | (bytes[pos + 1] & 0xFF) << 8 | (bytes[pos + 2] & 0xFF) << 16 | (bytes[pos + 3] & 0xFF) << 24;
		pos += 4;
		return result;
	}


	public final long getLong() {
		final long result =
				(bytes[pos] & 0xFF | (bytes[pos + 1] & 0xFF) << 8)
						| (bytes[pos + 2] & 0xFF) << 16 | (long) (bytes[pos + 3] & 0xFF) << 24
						| (long) (bytes[pos + 4] & 0xFF) << 32 | (long) (bytes[pos + 5] & 0xFF) << 40
						| (long) (bytes[pos + 6] & 0xFF) << 48 | (long) (bytes[pos + 7] & 0xFF) << 56;
		pos += 8;
		return result;
	}


	public final float getFloat() {
		return Float.intBitsToFloat(getInt());
	}


	public final double getDouble() {
		return Double.longBitsToDouble(getLong());
	}


	public final String getString() {
		final byte[] byteArray = getByteArray(getInt());
		return new String(byteArray, 0, byteArray.length, StandardCharsets.UTF_8);
	}


	// ======================================== PUT ======================================== //
	public final void putBoolean(final boolean value) {
		putByte((byte) (value ? 1 : 0));
	}


	public final void putByte(final byte value) {
		bytes[pos++] = value;
	}


	public final void putChar(final char value) {
		bytes[pos] = (byte) value;
		bytes[pos + 1] = (byte) (value >>> 8);
		pos += 2;
	}


	public final void putShort(final short value) {
		bytes[pos] = (byte) value;
		bytes[pos + 1] = (byte) (value >>> 8);
		pos += 2;
	}


	public final void putInt(final int value) {
		bytes[pos] = (byte) value;
		bytes[pos + 1] = (byte) (value >>> 8);
		bytes[pos + 2] = (byte) (value >>> 16);
		bytes[pos + 3] = (byte) (value >>> 24);
		pos += 4;
	}


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


	public final void putFloat(final float value) {
		putInt(Float.floatToIntBits(value));
	}


	public final void putDouble(final double value) {
		putLong(Double.doubleToLongBits(value));
	}


	public final void putString(final String value) {
		final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
		putInt(bytes.length);
		putByteArray(bytes);
	}


	// ====================================== GET_ARR ======================================== //
	public final boolean[] getBooleanArray(final int length) {
		final boolean[] out = new boolean[length];
		for (int i = 0; i < length; i++)
			out[i] = getByte() == 1;
		return out;
	}


	public final byte[] getByteArray(final int length) {
		final byte[] out = new byte[length];
		for (int i = 0; i < length; i++)
			out[i] = getByte();
		return out;
	}


	public final char[] getCharArray(final int length) {
		final char[] out = new char[length];
		for (int i = 0; i < length; i++)
			out[i] = getChar();
		return out;
	}


	public final short[] getShortArray(final int length) {
		final short[] out = new short[length];
		for (int i = 0; i < length; i++)
			out[i] = getShort();
		return out;
	}


	public final int[] getIntArray(final int length) {
		final int[] out = new int[length];
		for (int i = 0; i < length; i++)
			out[i] = getInt();
		return out;
	}


	public final long[] getLongArray(final int length) {
		final long[] out = new long[length];
		for (int i = 0; i < length; i++)
			out[i] = getLong();
		return out;
	}


	public final float[] getFloatArray(final int length) {
		final float[] out = new float[length];
		for (int i = 0; i < length; i++)
			out[i] = getFloat();
		return out;
	}


	public final double[] getDoubleArray(final int length) {
		final double[] out = new double[length];
		for (int i = 0; i < length; i++)
			out[i] = getDouble();
		return out;
	}


	public final String[] getStringArray(final int length) {
		final String[] out = new String[length];
		for (int i = 0; i < length; i++)
			out[i] = getString();
		return out;
	}


	// ====================================== PUT_ARR ======================================== //
	public final void putBooleanArray(final boolean[] value) {
		for (final boolean b : value) putByte((byte) (b ? 1 : 0));
	}


	public final void putByteArray(final byte[] value) {
		for (final byte b : value) putByte(b);
	}


	public final void putCharArray(final char[] value) {
		for (final char c : value) putChar(c);
	}


	public final void putShortArray(final short[] value) {
		for (final short s : value) putShort(s);
	}


	public final void putIntArray(final int[] value) {
		for (final int i : value) putInt(i);
	}


	public final void putLongArray(final long[] value) {
		for (final long l : value) putLong(l);
	}


	public final void putFloatArray(final float[] value) {
		for (final float f : value) putFloat(f);
	}


	public final void putDoubleArray(final double[] value) {
		for (final double d : value) putDouble(d);
	}


	public final void putStringArray(final String[] value) {
		for (final String s : value) putString(s);
	}
}
