package net.oskarstrom.hyphen.io;

/**
 * <h2>This is the créme de créme of all IO. Highly unsafe but really fast.</h2>
 */
@SuppressWarnings({"AccessStaticViaInstance", "FinalMethodInFinalClass"})
// if the jvm sees us import unsafe, it will explode:tm::tm:
public final class UnsafeIO {
	private static final sun.misc.Unsafe UNSAFE = net.oskarstrom.hyphen.util.UnsafeUtil.getUnsafeInstance();
	private static final int BOOLEAN_OFFSET = UNSAFE.ARRAY_BOOLEAN_BASE_OFFSET;
	private static final int BYTE_OFFSET = UNSAFE.ARRAY_BYTE_BASE_OFFSET;
	private static final int CHAR_OFFSET = UNSAFE.ARRAY_CHAR_BASE_OFFSET;
	private static final int SHORT_OFFSET = UNSAFE.ARRAY_SHORT_BASE_OFFSET;
	private static final int INT__OFFSET = UNSAFE.ARRAY_INT_BASE_OFFSET;
	private static final int LONG_OFFSET = UNSAFE.ARRAY_LONG_BASE_OFFSET;
	private static final int FLOAT_OFFSET = UNSAFE.ARRAY_FLOAT_BASE_OFFSET;
	private static final int DOUBLE_OFFSET = UNSAFE.ARRAY_DOUBLE_BASE_OFFSET;
	private final long address;
	private long currentAddress;

	private UnsafeIO(long address) {
		this.address = address;
		this.currentAddress = address;
	}

	public static UnsafeIO create(int size) {
		return new UnsafeIO(UNSAFE.allocateMemory(size));
	}


	public final boolean getBoolean() {
		return UNSAFE.getBoolean(null, currentAddress++ + BOOLEAN_OFFSET);
	}

	public final byte getByte() {
		return UNSAFE.getByte(null, currentAddress++ + BYTE_OFFSET);
	}

	public final char getChar() {
		final char c = UNSAFE.getChar(null, currentAddress + CHAR_OFFSET);
		currentAddress += 2;
		return c;
	}

	public final short getShort() {
		final short s = UNSAFE.getShort(null, currentAddress + SHORT_OFFSET);
		currentAddress += 2;
		return s;
	}

	public final int getInt() {
		final int i = UNSAFE.getInt(null, currentAddress + INT__OFFSET);
		currentAddress += 4;
		return i;
	}

	public final long getLong() {
		final long l = UNSAFE.getLong(null, currentAddress + LONG_OFFSET);
		currentAddress += 8;
		return l;
	}

	public final float getFloat() {
		final float f = UNSAFE.getFloat(null, currentAddress + FLOAT_OFFSET);
		currentAddress += 4;
		return f;
	}

	public final double getDouble() {
		final double f = UNSAFE.getDouble(null, currentAddress + DOUBLE_OFFSET);
		currentAddress += 8;
		return f;
	}

	public final void putBoolean(final boolean value) {
		UNSAFE.putBoolean(null, currentAddress++ + BOOLEAN_OFFSET, value);
	}

	public final void putByte(final byte value) {
		UNSAFE.putByte(null, currentAddress++ + BYTE_OFFSET, value);
	}


	public final void putChar(final char value) {
		UNSAFE.putChar(null, currentAddress + CHAR_OFFSET, value);
		currentAddress += 2;
	}


	public final void putShort(final short value) {
		UNSAFE.putShort(null, currentAddress + SHORT_OFFSET, value);
		currentAddress += 2;
	}


	public final void putInt(final int value) {
		UNSAFE.putInt(null, currentAddress + INT__OFFSET, value);
		currentAddress += 4;
	}


	public final void putLong(final long value) {
		UNSAFE.putLong(null, currentAddress + LONG_OFFSET, value);
		currentAddress += 8;
	}


	public final void putFloat(final float value) {
		UNSAFE.putFloat(null, currentAddress + FLOAT_OFFSET, value);
		currentAddress += 4;
	}


	public final void putDouble(final double value) {
		UNSAFE.putDouble(null, currentAddress + DOUBLE_OFFSET, value);
		currentAddress += 8;
	}


	public final boolean[] getBooleanArray(final int bytes) {
		final boolean[] array = new boolean[bytes];
		UNSAFE.copyMemory(null, currentAddress + BYTE_OFFSET, array, BOOLEAN_OFFSET, bytes);
		currentAddress += bytes;
		return array;
	}


	public final byte[] getByteArray(final int bytes) {
		final byte[] array = new byte[bytes];
		UNSAFE.copyMemory(null, currentAddress + BYTE_OFFSET, array, BYTE_OFFSET, bytes);
		currentAddress += bytes;
		return array;
	}


	public final char[] getCharArray(final int length) {
		final char[] array = new char[length];
		final int bytes = length * 2;
		UNSAFE.copyMemory(null, currentAddress + BYTE_OFFSET, array, CHAR_OFFSET, bytes);
		currentAddress += bytes;
		return array;
	}


	public final short[] getShortArray(final int length) {
		final short[] array = new short[length];
		final int bytes = length * 2;
		UNSAFE.copyMemory(null, currentAddress + BYTE_OFFSET, array, SHORT_OFFSET, bytes);
		currentAddress += bytes;
		return array;
	}


	public final int[] getIntArray(final int length) {
		final int[] array = new int[length];
		final int bytes = length * 4;
		UNSAFE.copyMemory(null, currentAddress + BYTE_OFFSET, array, INT__OFFSET, bytes);
		currentAddress += bytes;
		return array;
	}


	public final long[] getLongArray(final int length) {
		final long[] array = new long[length];
		final int bytes = length * 8;
		UNSAFE.copyMemory(null, currentAddress + BYTE_OFFSET, array, LONG_OFFSET, bytes);
		currentAddress += bytes;
		return array;
	}


	public final float[] getFloatArray(final int length) {
		final float[] array = new float[length];
		final int bytes = length * 4;
		UNSAFE.copyMemory(null, currentAddress + BYTE_OFFSET, array, FLOAT_OFFSET, bytes);
		currentAddress += bytes;
		return array;
	}


	public final double[] getDoubleArray(final int length) {
		final double[] array = new double[length];
		final int bytes = length * 8;
		UNSAFE.copyMemory(null, currentAddress + BYTE_OFFSET, array, DOUBLE_OFFSET, bytes);
		currentAddress += bytes;
		return array;
	}


	public final void putBooleanArray(final boolean[] value) {
		final int bytes = value.length;
		UNSAFE.copyMemory(value, BOOLEAN_OFFSET, null, currentAddress + BYTE_OFFSET, bytes);
		currentAddress += bytes;
	}


	public final void putByteArray(final byte[] value) {
		final int bytes = value.length;
		UNSAFE.copyMemory(value, BYTE_OFFSET, null, currentAddress + BYTE_OFFSET, bytes);
		currentAddress += bytes;
	}


	public final void putCharArray(final char[] value) {
		final int bytes = value.length * 2;
		UNSAFE.copyMemory(value, CHAR_OFFSET, null, currentAddress + BYTE_OFFSET, bytes);
		currentAddress += bytes;
	}


	public final void putShortArray(final short[] value) {
		final int bytes = value.length * 2;
		UNSAFE.copyMemory(value, SHORT_OFFSET, null, currentAddress + BYTE_OFFSET, bytes);
		currentAddress += bytes;
	}


	public final void putIntArray(final int[] value) {
		final int bytes = value.length * 4;
		UNSAFE.copyMemory(value, INT__OFFSET, null, currentAddress + BYTE_OFFSET, bytes);
		currentAddress += bytes;
	}


	public final void putLongArray(final long[] value) {
		final int bytes = value.length * 8;
		UNSAFE.copyMemory(value, LONG_OFFSET, null, currentAddress + BYTE_OFFSET, bytes);
		currentAddress += bytes;
	}


	public final void putFloatArray(final float[] value) {
		final int bytes = value.length * 4;
		UNSAFE.copyMemory(value, FLOAT_OFFSET, null, currentAddress + BYTE_OFFSET, bytes);
		currentAddress += bytes;
	}


	public final void putDoubleArray(final double[] value) {
		final int bytes = value.length * 8;
		UNSAFE.copyMemory(value, DOUBLE_OFFSET, null, currentAddress + BYTE_OFFSET, bytes);
		currentAddress += bytes;
	}


	public final void rewind() {
		currentAddress = address;
	}


	public final int pos() {
		return (int) ((int) currentAddress - address);
	}


}
