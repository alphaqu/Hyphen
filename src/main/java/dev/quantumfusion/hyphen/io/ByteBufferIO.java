package dev.quantumfusion.hyphen.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * <h2>Useful for debug and when UnsafeIO is unavailable.</h2>
 */
public final class ByteBufferIO implements IOInterface {
	private final ByteBuffer byteBuffer;

	private ByteBufferIO(ByteBuffer buffer) {
		this.byteBuffer = buffer;
	}

	public static ByteBufferIO create(int size) {
		return new ByteBufferIO(ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN));
	}

	public static ByteBufferIO createDirect(int size) {
		return new ByteBufferIO(ByteBuffer.allocateDirect(size).order(ByteOrder.LITTLE_ENDIAN));
	}

	@Override
	public boolean getBoolean() {
		return byteBuffer.get() == 1;
	}

	@Override
	public byte getByte() {
		return byteBuffer.get();
	}

	@Override
	public char getChar() {
		return byteBuffer.getChar();
	}

	@Override
	public short getShort() {
		return byteBuffer.getShort();
	}

	@Override
	public int getInt() {
		return byteBuffer.getInt();
	}

	@Override
	public long getLong() {
		return byteBuffer.getLong();
	}

	@Override
	public float getFloat() {
		return byteBuffer.getFloat();
	}

	@Override
	public double getDouble() {
		return byteBuffer.getDouble();
	}

	@Override
	public String getString() {
		final byte[] byteArray = getByteArray(byteBuffer.getInt());
		return new String(byteArray, 0, byteArray.length, StandardCharsets.UTF_8);
	}

	@Override
	public boolean[] getBooleanArray(int length) {
		final boolean[] out = new boolean[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.get() == 1;
		return out;
	}

	@Override
	public byte[] getByteArray(int length) {
		byte[] out = new byte[length];
		byteBuffer.get(out, 0, length);
		return out;
	}

	@Override
	public char[] getCharArray(int length) {
		final char[] out = new char[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getChar();
		return out;
	}

	@Override
	public short[] getShortArray(int length) {
		final short[] out = new short[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getShort();
		return out;
	}

	@Override
	public int[] getIntArray(int length) {
		final int[] out = new int[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getInt();
		return out;
	}

	@Override
	public long[] getLongArray(int length) {
		final long[] out = new long[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getLong();
		return out;
	}

	@Override
	public float[] getFloatArray(int length) {
		final float[] out = new float[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getFloat();
		return out;
	}

	@Override
	public double[] getDoubleArray(int length) {
		final double[] out = new double[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getDouble();
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
		byteBuffer.put((byte) (value ? 1 : 0));

	}

	@Override
	public void putByte(byte value) {
		byteBuffer.put(value);

	}

	@Override
	public void putChar(char value) {
		byteBuffer.putChar(value);

	}

	@Override
	public void putShort(short value) {
		byteBuffer.putShort(value);

	}

	@Override
	public void putInt(int value) {
		byteBuffer.putInt(value);

	}

	@Override
	public void putLong(long value) {
		byteBuffer.putLong(value);

	}

	@Override
	public void putFloat(float value) {
		byteBuffer.putFloat(value);

	}

	@Override
	public void putDouble(double value) {
		byteBuffer.putDouble(value);

	}

	@Override
	public void putString(String value) {
		byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
		putInt(bytes.length);
		putByteArray(bytes);
	}

	@Override
	public void putBooleanArray(boolean[] value) {
		for (boolean b : value) byteBuffer.put((byte) (b ? 1 : 0));

	}

	@Override
	public void putByteArray(byte[] value) {
		for (byte b : value) byteBuffer.put(b);

	}

	@Override
	public void putCharArray(char[] value) {
		for (char c : value) byteBuffer.putChar(c);

	}

	@Override
	public void putShortArray(short[] value) {
		for (short s : value) byteBuffer.putShort(s);

	}

	@Override
	public void putIntArray(int[] value) {
		for (int i : value) byteBuffer.putInt(i);

	}

	@Override
	public void putLongArray(long[] value) {
		for (long l : value) byteBuffer.putLong(l);

	}

	@Override
	public void putFloatArray(float[] value) {
		for (float f : value) byteBuffer.putFloat(f);

	}

	@Override
	public void putDoubleArray(double[] value) {
		for (double d : value) byteBuffer.putDouble(d);

	}

	@Override
	public void putStringArray(String[] value) {
		for (String s : value) putString(s);
	}

	@Override
	public void rewind() {
		byteBuffer.rewind();
	}

	@Override
	public int pos() {
		return byteBuffer.position();
	}

	public final void close() {
		byteBuffer.clear();
	}
}
