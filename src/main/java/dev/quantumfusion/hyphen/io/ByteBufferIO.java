package dev.quantumfusion.hyphen.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * <h2>Useful for debug and when UnsafeIO is unavailable.</h2>
 */
@SuppressWarnings({"FinalMethodInFinalClass", "FinalStaticMethod"})
public final class ByteBufferIO implements IOInterface {
	private final ByteBuffer byteBuffer;

	private ByteBufferIO(final ByteBuffer buffer) {
		this.byteBuffer = buffer;
	}

	public static final ByteBufferIO create(final int size) {
		return new ByteBufferIO(ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN));
	}

	public static final ByteBufferIO createDirect(final int size) {
		return new ByteBufferIO(ByteBuffer.allocateDirect(size).order(ByteOrder.LITTLE_ENDIAN));
	}

	// ======================================= FUNC ======================================= //
	public final void rewind() {
		byteBuffer.rewind();
	}


	public final int pos() {
		return byteBuffer.position();
	}

	public final void close() {
		byteBuffer.clear();
	}


	// ======================================== GET ======================================== //
	public final boolean getBoolean() {
		return byteBuffer.get() == 1;
	}


	public final byte getByte() {
		return byteBuffer.get();
	}


	public final char getChar() {
		return byteBuffer.getChar();
	}


	public final short getShort() {
		return byteBuffer.getShort();
	}


	public final int getInt() {
		return byteBuffer.getInt();
	}


	public final long getLong() {
		return byteBuffer.getLong();
	}


	public final float getFloat() {
		return byteBuffer.getFloat();
	}


	public final double getDouble() {
		return byteBuffer.getDouble();
	}


	public final String getString() {
		final byte[] byteArray = getByteArray(byteBuffer.getInt());
		return new String(byteArray, 0, byteArray.length, StandardCharsets.UTF_8);
	}


	// ======================================== PUT ======================================== //
	public final void putBoolean(final boolean value) {
		byteBuffer.put((byte) (value ? 1 : 0));
	}


	public final void putByte(final byte value) {
		byteBuffer.put(value);
	}


	public final void putChar(final char value) {
		byteBuffer.putChar(value);
	}


	public final void putShort(final short value) {
		byteBuffer.putShort(value);
	}

	public final void putInt(final int value) {
		byteBuffer.putInt(value);
	}


	public final void putLong(final long value) {
		byteBuffer.putLong(value);
	}


	public final void putFloat(final float value) {
		byteBuffer.putFloat(value);
	}


	public final void putDouble(final double value) {
		byteBuffer.putDouble(value);
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
			out[i] = byteBuffer.get() == 1;
		return out;
	}


	public final byte[] getByteArray(final int length) {
		final byte[] out = new byte[length];
		byteBuffer.get(out, 0, length);
		return out;
	}


	public final char[] getCharArray(final int length) {
		final char[] out = new char[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getChar();
		return out;
	}

	public final short[] getShortArray(final int length) {
		final short[] out = new short[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getShort();
		return out;
	}

	public final int[] getIntArray(final int length) {
		final int[] out = new int[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getInt();
		return out;
	}

	public final long[] getLongArray(final int length) {
		final long[] out = new long[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getLong();
		return out;
	}

	public final float[] getFloatArray(final int length) {
		final float[] out = new float[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getFloat();
		return out;
	}

	public final double[] getDoubleArray(final int length) {
		final double[] out = new double[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getDouble();
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
		for (final boolean b : value) byteBuffer.put((byte) (b ? 1 : 0));
	}


	public final void putByteArray(final byte[] value) {
		for (final byte b : value) byteBuffer.put(b);
	}


	public final void putCharArray(final char[] value) {
		for (final char c : value) byteBuffer.putChar(c);
	}


	public final void putShortArray(final short[] value) {
		for (final short s : value) byteBuffer.putShort(s);
	}


	public final void putIntArray(final int[] value) {
		for (final int i : value) byteBuffer.putInt(i);
	}


	public final void putLongArray(final long[] value) {
		for (final long l : value) byteBuffer.putLong(l);
	}


	public final void putFloatArray(final float[] value) {
		for (final float f : value) byteBuffer.putFloat(f);
	}


	public final void putDoubleArray(final double[] value) {
		for (final double d : value) byteBuffer.putDouble(d);
	}


	public final void putStringArray(final String[] value) {
		for (final String s : value) putString(s);
	}
}
