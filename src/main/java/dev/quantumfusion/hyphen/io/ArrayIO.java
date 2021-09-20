package dev.quantumfusion.hyphen.io;

import java.nio.charset.StandardCharsets;

public class ArrayIO implements IOInterface {
	private final byte[] bytes;
	private int pos;

	private ArrayIO(byte[] bytes, int pos) {
		this.bytes = bytes;
		this.pos = pos;
	}

	public static ArrayIO create(int size) {
		return new ArrayIO(new byte[size], 0);
	}

	@Override
	public boolean getBoolean() {
		return getByte() != 0;
	}

	@Override
	public byte getByte() {
		return bytes[pos++];
	}

	@Override
	public char getChar() {
		final char c = (char) (bytes[pos] & 0xFF | (bytes[pos + 1] & 0xFF) << 8);
		pos += 2;
		return c;
	}

	@Override
	public short getShort() {
		final short c = (short) (bytes[pos] & 0xFF | (bytes[pos + 1] & 0xFF) << 8);
		pos += 2;
		return c;
	}

	@Override
	public int getInt() {
		final int result = bytes[pos] & 0xFF | (bytes[pos + 1] & 0xFF) << 8 | (bytes[pos + 2] & 0xFF) << 16 | (bytes[pos + 3] & 0xFF) << 24;
		pos += 4;
		return result;
	}

	@Override
	public long getLong() {
		final long result =
				(bytes[pos] & 0xFF | (bytes[pos + 1] & 0xFF) << 8)
						| (bytes[pos + 2] & 0xFF) << 16 | (long) (bytes[pos + 3] & 0xFF) << 24
						| (long) (bytes[pos + 4] & 0xFF) << 32 | (long) (bytes[pos + 5] & 0xFF) << 40
						| (long) (bytes[pos + 6] & 0xFF) << 48 | (long) (bytes[pos + 7] & 0xFF) << 56;
		pos += 8;
		return result;
	}

	@Override
	public float getFloat() {
		return Float.intBitsToFloat(getInt());
	}

	@Override
	public double getDouble() {
		return Double.longBitsToDouble(getLong());
	}

	@Override
	public String getString() {
		final byte[] byteArray = getByteArray(getInt());
		return new String(byteArray, 0, byteArray.length, StandardCharsets.UTF_8);
	}

	@Override
	public boolean[] getBooleanArray(int length) {
		final boolean[] out = new boolean[length];
		for (int i = 0; i < length; i++)
			out[i] = getByte() == 1;
		return out;
	}

	@Override
	public byte[] getByteArray(int length) {
		final byte[] out = new byte[length];
		for (int i = 0; i < length; i++)
			out[i] = getByte();
		return out;
	}

	@Override
	public char[] getCharArray(int length) {
		final char[] out = new char[length];
		for (int i = 0; i < length; i++)
			out[i] = getChar();
		return out;
	}

	@Override
	public short[] getShortArray(int length) {
		final short[] out = new short[length];
		for (int i = 0; i < length; i++)
			out[i] = getShort();
		return out;
	}

	@Override
	public int[] getIntArray(int length) {
		final int[] out = new int[length];
		for (int i = 0; i < length; i++)
			out[i] = getInt();
		return out;
	}

	@Override
	public long[] getLongArray(int length) {
		final long[] out = new long[length];
		for (int i = 0; i < length; i++)
			out[i] = getLong();
		return out;
	}

	@Override
	public float[] getFloatArray(int length) {
		final float[] out = new float[length];
		for (int i = 0; i < length; i++)
			out[i] = getFloat();
		return out;
	}

	@Override
	public double[] getDoubleArray(int length) {
		final double[] out = new double[length];
		for (int i = 0; i < length; i++)
			out[i] = getDouble();
		return out;
	}

	@Override
	public String[] getStringArray(int length) {
		final String[] out = new String[length];
		for (int i = 0; i < length; i++)
			out[i] = getString();
		return out;
	}

	@Override
	public void putBoolean(boolean value) {
		putByte((byte) (value ? 1 : 0));
	}

	@Override
	public void putByte(byte value) {
		bytes[pos++] = value;
	}

	@Override
	public void putChar(char value) {
		bytes[pos] = (byte) value;
		bytes[pos + 1] = (byte) (value >>> 8);
		pos += 2;
	}

	@Override
	public void putShort(short value) {
		bytes[pos] = (byte) value;
		bytes[pos + 1] = (byte) (value >>> 8);
		pos += 2;
	}

	@Override
	public void putInt(int value) {
		bytes[pos] = (byte) value;
		bytes[pos + 1] = (byte) (value >>> 8);
		bytes[pos + 2] = (byte) (value >>> 16);
		bytes[pos + 3] = (byte) (value >>> 24);
		pos += 4;
	}

	@Override
	public void putLong(long value) {
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
	public void putFloat(float value) {
		putInt(Float.floatToIntBits(value));
	}

	@Override
	public void putDouble(double value) {
		putLong(Double.doubleToLongBits(value));
	}

	@Override
	public void putString(String value) {
		byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
		putInt(bytes.length);
		putByteArray(bytes);
	}

	@Override
	public void putBooleanArray(boolean[] value) {
		for (boolean b : value) putByte((byte) (b ? 1 : 0));

	}

	@Override
	public void putByteArray(byte[] value) {
		for (byte b : value) putByte(b);

	}

	@Override
	public void putCharArray(char[] value) {
		for (char c : value) putChar(c);

	}

	@Override
	public void putShortArray(short[] value) {
		for (short s : value) putShort(s);

	}

	@Override
	public void putIntArray(int[] value) {
		for (int i : value) putInt(i);

	}

	@Override
	public void putLongArray(long[] value) {
		for (long l : value) putLong(l);

	}

	@Override
	public void putFloatArray(float[] value) {
		for (float f : value) putFloat(f);

	}

	@Override
	public void putDoubleArray(double[] value) {
		for (double d : value) putDouble(d);

	}

	@Override
	public void putStringArray(String[] value) {
		for (String s : value) putString(s);
	}

	@Override
	public void rewind() {
		pos = 0;
	}

	@Override
	public int pos() {
		return pos;
	}
}
