package net.oskarstrom.hyphen.io;

import java.nio.ByteBuffer;

public final class HeapBufferIO implements IOInterface {
	private final ByteBuffer byteBuffer;

	public HeapBufferIO(int size) {
		this.byteBuffer = ByteBuffer.allocate(size);
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
	public boolean[] getBooleanArray(int length) {
		final boolean[] out = new boolean[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.get() == 1;
		return out;
	}

	@Override
	public byte[] getByteArray(int length) {
		byte[] out = new byte[length];
		byteBuffer.get(out, byteBuffer.position(), length);
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
	public IOInterface putBoolean(boolean value) {
		byteBuffer.put((byte) (value ? 1 : 0));
		return this;
	}

	@Override
	public IOInterface putByte(byte value) {
		byteBuffer.put(value);
		return this;
	}

	@Override
	public IOInterface putChar(char value) {
		byteBuffer.putChar(value);
		return this;
	}

	@Override
	public IOInterface putShort(short value) {
		byteBuffer.putShort(value);
		return this;
	}

	@Override
	public IOInterface putInt(int value) {
		byteBuffer.putInt(value);
		return this;
	}

	@Override
	public IOInterface putLong(long value) {
		byteBuffer.putLong(value);
		return this;
	}

	@Override
	public IOInterface putFloat(float value) {
		byteBuffer.putFloat(value);
		return this;
	}

	@Override
	public IOInterface putDouble(double value) {
		byteBuffer.putDouble(value);
		return this;
	}

	@Override
	public IOInterface putBooleanArray(boolean[] value) {
		for (boolean b : value) byteBuffer.put((byte) (b ? 1 : 0));
		return this;
	}

	@Override
	public IOInterface putByteArray(byte[] value) {
		for (byte b : value) byteBuffer.put(b);
		return this;
	}

	@Override
	public IOInterface putCharArray(char[] value) {
		for (char c : value) byteBuffer.putChar(c);
		return this;
	}

	@Override
	public IOInterface putShortArray(short[] value) {
		for (short s : value) byteBuffer.putShort(s);
		return this;
	}

	@Override
	public IOInterface putIntArray(int[] value) {
		for (int i : value) byteBuffer.putInt(i);
		return this;
	}

	@Override
	public IOInterface putLongArray(long[] value) {
		for (long l : value) byteBuffer.putLong(l);
		return this;
	}

	@Override
	public IOInterface putFloatArray(float[] value) {
		for (float f : value) byteBuffer.putFloat(f);
		return this;
	}

	@Override
	public IOInterface putDoubleArray(double[] value) {
		for (double d : value) byteBuffer.putDouble(d);
		return this;
	}

	@Override
	public void rewind() {
		byteBuffer.rewind();
	}
}
