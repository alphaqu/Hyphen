package net.oskarstrom.hyphen.io;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@SuppressWarnings("AccessStaticViaInstance") // if the jvm sees us import unsafe, it will explode:tm::tm:
public final class UnsafeIO implements IOInterface {
	private static final sun.misc.Unsafe UNSAFE = getUnsafeInstance();
	private static final int booleanOffset = UNSAFE.ARRAY_BOOLEAN_BASE_OFFSET;
	private static final int byteOffset = UNSAFE.ARRAY_BYTE_BASE_OFFSET;
	private static final int charOffset = UNSAFE.ARRAY_CHAR_BASE_OFFSET;
	private static final int shortOffset = UNSAFE.ARRAY_SHORT_BASE_OFFSET;
	private static final int intOffset = UNSAFE.ARRAY_INT_BASE_OFFSET;
	private static final int longOffset = UNSAFE.ARRAY_LONG_BASE_OFFSET;
	private static final int floatOffset = UNSAFE.ARRAY_FLOAT_BASE_OFFSET;
	private static final int doubleOffset = UNSAFE.ARRAY_DOUBLE_BASE_OFFSET;
	private final long address;
	private int pos = 0;


	public UnsafeIO(long size) {
		this.address = UNSAFE.allocateMemory(size);
	}

	private static sun.misc.Unsafe getUnsafeInstance() {
		Class<sun.misc.Unsafe> clazz = sun.misc.Unsafe.class;
		for (Field field : clazz.getDeclaredFields()) {
			if (!field.getType().equals(clazz))
				continue;
			final int modifiers = field.getModifiers();
			if (!(Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)))
				continue;
			try {
				field.setAccessible(true);
				return (sun.misc.Unsafe) field.get(null);
			} catch (Exception ignored) {
			}
			break;
		}

		throw new IllegalStateException("Unsafe is unavailable.");
	}

	// Boolean
	public boolean getBoolean() {
		return UNSAFE.getBoolean(null, address + pos++);
	}

	public IOInterface putBoolean(boolean value) {
		UNSAFE.putBoolean(null, address + pos++, value);
		return this;
	}

	// Bytes
	public byte getByte() {
		return UNSAFE.getByte(address + pos++);
	}

	public UnsafeIO putByte(byte b) {
		UNSAFE.putByte(address + pos++, b);
		return this;
	}


	// Chars
	public char getChar() {
		final var c = UNSAFE.getChar(address + pos);
		pos += 2;
		return c;
	}

	public UnsafeIO putChar(char value) {
		UNSAFE.putChar(address + pos, value);
		pos += 2;
		return this;
	}


	// Shorts
	public short getShort() {
		final var s = UNSAFE.getShort(address + pos);
		pos += 2;
		return s;
	}


	public UnsafeIO putShort(short value) {
		UNSAFE.putShort(address + pos, value);
		pos += 2;
		return this;
	}


	// Ints
	public int getInt() {
		final var i = UNSAFE.getInt(address + pos);
		pos += 4;
		return i;
	}


	public UnsafeIO putInt(int value) {
		UNSAFE.putInt(address + pos, value);
		pos += 4;
		return this;
	}


	// Longs
	public long getLong() {
		final var l = UNSAFE.getLong(address + pos);
		pos += 8;
		return l;
	}


	public UnsafeIO putLong(long value) {
		UNSAFE.putLong(address + pos, value);
		pos += 8;
		return this;
	}


	// Floats
	public float getFloat() {
		final var f = UNSAFE.getFloat(address + pos);
		pos += 4;
		return f;
	}


	public UnsafeIO putFloat(float value) {
		UNSAFE.putFloat(address + pos, value);
		pos += 4;
		return this;
	}


	// Doubles
	public double getDouble() {
		final var f = UNSAFE.getDouble(address + pos);
		pos += 8;
		return f;
	}

	public UnsafeIO putDouble(double value) {
		UNSAFE.putDouble(address + pos, value);
		pos += 8;
		return this;
	}

	@Override
	public boolean[] getBooleanArray(int length) {
		boolean[] array = new boolean[length];
		UNSAFE.copyMemory(null, address + pos + byteOffset, array, booleanOffset, length);
		return array;
	}

	@Override
	public byte[] getByteArray(int length) {
		byte[] array = new byte[length];
		UNSAFE.copyMemory(null, address + pos + byteOffset, array, byteOffset, length);
		pos += length;
		return array;
	}

	@Override
	public char[] getCharArray(int length) {
		final char[] array = new char[length];
		final int bytes = length * 2;
		UNSAFE.copyMemory(null, address + pos + byteOffset, array, charOffset, bytes);
		pos += bytes;
		return array;
	}

	@Override
	public short[] getShortArray(int length) {
		final short[] array = new short[length];
		final int bytes = length * 2;
		UNSAFE.copyMemory(null, address + pos + byteOffset, array, shortOffset, bytes);
		pos += bytes;
		return array;
	}

	@Override
	public int[] getIntArray(int length) {
		final int[] array = new int[length];
		final int bytes = length * 4;
		UNSAFE.copyMemory(null, address + pos + byteOffset, array, intOffset, bytes);
		pos += bytes;
		return array;
	}

	@Override
	public long[] getLongArray(int length) {
		final long[] array = new long[length];
		final int bytes = length * 8;
		UNSAFE.copyMemory(null, address + pos + byteOffset, array, longOffset, bytes);
		pos += bytes;
		return array;
	}

	@Override
	public float[] getFloatArray(int length) {
		final float[] array = new float[length];
		final int bytes = length * 4;
		UNSAFE.copyMemory(null, address + pos + byteOffset, array, floatOffset, bytes);
		pos += bytes;
		return array;
	}

	@Override
	public double[] getDoubleArray(int length) {
		final double[] array = new double[length];
		final int bytes = length * 8;
		UNSAFE.copyMemory(null, address + pos + byteOffset, array, doubleOffset, bytes);
		pos += bytes;
		return array;
	}


	@Override
	public IOInterface putBooleanArray(boolean[] value) {
		final int length = value.length;
		UNSAFE.copyMemory(value, booleanOffset, null, address + pos + byteOffset, length);
		pos += length;
		return this;
	}

	@Override
	public IOInterface putByteArray(byte[] value) {
		final int length = value.length;
		UNSAFE.copyMemory(value, byteOffset, null, address + pos + byteOffset, length);
		pos += length;
		return this;
	}

	@Override
	public IOInterface putCharArray(char[] value) {
		final int length = value.length * 2;
		UNSAFE.copyMemory(value, charOffset, null, address + pos + byteOffset, length);
		pos += length;
		return this;
	}

	@Override
	public IOInterface putShortArray(short[] value) {
		final int length = value.length * 2;
		UNSAFE.copyMemory(value, shortOffset, null, address + pos + byteOffset, length);
		pos += length;
		return this;
	}

	@Override
	public IOInterface putIntArray(int[] value) {
		final int length = value.length * 4;
		UNSAFE.copyMemory(value, intOffset, null, address + pos + byteOffset, length);
		pos += length;
		return this;
	}

	@Override
	public IOInterface putLongArray(long[] value) {
		final int length = value.length * 8;
		UNSAFE.copyMemory(value, longOffset, null, address + pos + byteOffset, length);
		pos += length;
		return this;
	}

	@Override
	public IOInterface putFloatArray(float[] value) {
		final int length = value.length * 4;
		UNSAFE.copyMemory(value, floatOffset, null, address + pos + byteOffset, length);
		pos += length;
		return this;
	}

	@Override
	public IOInterface putDoubleArray(double[] value) {
		final int length = value.length * 8;
		UNSAFE.copyMemory(value, doubleOffset, null, address + pos + byteOffset, length);
		pos += length;
		return this;
	}

	@Override
	public void rewind() {
		pos = 0;
	}


}
