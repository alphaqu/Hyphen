package dev.quantumfusion.hyphen.io;

import dev.quantumfusion.hyphen.HyphenSerializer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * <h2>Useful for debug and when UnsafeIO is unavailable.</h2>
 */
@SuppressWarnings({"FinalMethodInFinalClass", "FinalStaticMethod", "unused"})
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

	public static final <O> ByteBufferIO create(final HyphenSerializer<ByteBufferIO, O> serializer, final O data) {
		return create(serializer.measure(data));
	}

	public static final <O> ByteBufferIO createDirect(final HyphenSerializer<ByteBufferIO, O> serializer, final O data) {
		return createDirect(serializer.measure(data));
	}

	// ======================================= FUNC ======================================= //
	@Override
	public final void rewind() {
		byteBuffer.rewind();
	}


	@Override
	public final int pos() {
		return byteBuffer.position();
	}

	@Override
	public final void close() {
		byteBuffer.clear();
	}


	// ======================================== GET ======================================== //
	@Override
	public final boolean getBoolean() {
		return byteBuffer.get() == 1;
	}


	@Override
	public final byte getByte() {
		return byteBuffer.get();
	}


	@Override
	public final char getChar() {
		return byteBuffer.getChar();
	}


	@Override
	public final short getShort() {
		return byteBuffer.getShort();
	}


	@Override
	public final int getInt() {
		return byteBuffer.getInt();
	}


	@Override
	public final long getLong() {
		return byteBuffer.getLong();
	}


	@Override
	public final float getFloat() {
		return byteBuffer.getFloat();
	}


	@Override
	public final double getDouble() {
		return byteBuffer.getDouble();
	}


	@Override
	public final String getString() {
		final byte[] byteArray = getByteArray();
		return new String(byteArray, 0, byteArray.length, StandardCharsets.UTF_8);
	}


	// ======================================== PUT ======================================== //
	@Override
	public final void putBoolean(final boolean value) {
		byteBuffer.put((byte) (value ? 1 : 0));
	}


	@Override
	public final void putByte(final byte value) {
		byteBuffer.put(value);
	}


	@Override
	public final void putChar(final char value) {
		byteBuffer.putChar(value);
	}


	@Override
	public final void putShort(final short value) {
		byteBuffer.putShort(value);
	}

	@Override
	public final void putInt(final int value) {
		byteBuffer.putInt(value);
	}


	@Override
	public final void putLong(final long value) {
		byteBuffer.putLong(value);
	}


	@Override
	public final void putFloat(final float value) {
		byteBuffer.putFloat(value);
	}


	@Override
	public final void putDouble(final double value) {
		byteBuffer.putDouble(value);
	}


	@Override
	public final void putString(final String value) {
		putByteArray(value.getBytes(StandardCharsets.UTF_8));
	}


	// ====================================== GET_ARR ======================================== //
	@Override
	public final boolean[] getBooleanArray() {
		final int length = getInt();
		final boolean[] out = new boolean[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.get() == 1;
		return out;
	}


	@Override
	public final byte[] getByteArray() {
		final int length = getInt();
		final byte[] out = new byte[length];
		byteBuffer.get(out, 0, length);
		return out;
	}


	@Override
	public final char[] getCharArray() {
		final int length = getInt();
		final char[] out = new char[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getChar();
		return out;
	}

	@Override
	public final short[] getShortArray() {
		final int length = getInt();
		final short[] out = new short[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getShort();
		return out;
	}

	@Override
	public final int[] getIntArray() {
		final int length = getInt();
		final int[] out = new int[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getInt();
		return out;
	}

	@Override
	public final long[] getLongArray() {
		final int length = getInt();
		final long[] out = new long[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getLong();
		return out;
	}

	@Override
	public final float[] getFloatArray() {
		final int length = getInt();
		final float[] out = new float[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getFloat();
		return out;
	}

	@Override
	public final double[] getDoubleArray() {
		final int length = getInt();
		final double[] out = new double[length];
		for (int i = 0; i < length; i++)
			out[i] = byteBuffer.getDouble();
		return out;
	}

	@Override
	public final String[] getStringArray() {
		final int length = getInt();
		final String[] out = new String[length];
		for (int i = 0; i < length; i++)
			out[i] = getString();
		return out;
	}


	// ====================================== PUT_ARR ======================================== //
	@Override
	public final void putBooleanArray(final boolean[] value) {
		putInt(value.length);
		for (final boolean b : value) byteBuffer.put((byte) (b ? 1 : 0));
	}


	@Override
	public final void putByteArray(final byte[] value) {
		putInt(value.length);
		for (final byte b : value) byteBuffer.put(b);
	}


	@Override
	public final void putCharArray(final char[] value) {
		putInt(value.length);
		for (final char c : value) byteBuffer.putChar(c);
	}


	@Override
	public final void putShortArray(final short[] value) {
		putInt(value.length);
		for (final short s : value) byteBuffer.putShort(s);
	}


	@Override
	public final void putIntArray(final int[] value) {
		putInt(value.length);
		for (final int i : value) byteBuffer.putInt(i);
	}


	@Override
	public final void putLongArray(final long[] value) {
		putInt(value.length);
		for (final long l : value) byteBuffer.putLong(l);
	}


	@Override
	public final void putFloatArray(final float[] value) {
		putInt(value.length);
		for (final float f : value) byteBuffer.putFloat(f);
	}


	@Override
	public final void putDoubleArray(final double[] value) {
		putInt(value.length);
		for (final double d : value) byteBuffer.putDouble(d);
	}


	@Override
	public final void putStringArray(final String[] value) {
		putInt(value.length);
		for (final String s : value) putString(s);
	}
}
