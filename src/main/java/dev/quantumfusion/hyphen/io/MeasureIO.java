package dev.quantumfusion.hyphen.io;

@SuppressWarnings("FinalMethodInFinalClass")
public final class MeasureIO implements IOInterface {
	public int currentSize = 0;

	public final boolean getBoolean() {
		throw new UnsupportedOperationException();
	}

	public final byte getByte() {
		throw new UnsupportedOperationException();
	}

	public final char getChar() {
		throw new UnsupportedOperationException();
	}

	public final short getShort() {
		throw new UnsupportedOperationException();
	}

	public final int getInt() {
		throw new UnsupportedOperationException();
	}

	public final long getLong() {
		throw new UnsupportedOperationException();
	}

	public final float getFloat() {
		throw new UnsupportedOperationException();
	}

	public final double getDouble() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getString() {
		throw new UnsupportedOperationException();
	}

	public final void putBoolean(final boolean value) {
		currentSize++;
	}

	public final void putByte(final byte value) {
		currentSize++;
	}


	public final void putChar(final char value) {
		currentSize += 2;
	}


	public final void putShort(final short value) {
		currentSize += 2;
	}


	public final void putInt(final int value) {
		currentSize += 4;
	}


	public final void putLong(final long value) {
		currentSize += 8;
	}


	public final void putFloat(final float value) {
		currentSize += 4;
	}


	public final void putDouble(final double value) {
		currentSize += 8;
	}

	public final void putString(String value) {
		currentSize += value.length() * 2 + 4;
	}


	public final boolean[] getBooleanArray(final int bytes) {
		throw new UnsupportedOperationException();
	}


	public final byte[] getByteArray(final int bytes) {
		throw new UnsupportedOperationException();
	}


	public final char[] getCharArray(final int length) {
		throw new UnsupportedOperationException();
	}


	public final short[] getShortArray(final int length) {
		throw new UnsupportedOperationException();
	}


	public final int[] getIntArray(final int length) {
		throw new UnsupportedOperationException();
	}


	public final long[] getLongArray(final int length) {
		throw new UnsupportedOperationException();
	}


	public final float[] getFloatArray(final int length) {
		throw new UnsupportedOperationException();
	}


	public final double[] getDoubleArray(final int length) {
		throw new UnsupportedOperationException();
	}

	public final String[] getStringArray(int length) {
		throw new UnsupportedOperationException();
	}


	public final void putBooleanArray(final boolean[] value) {
		currentSize += value.length;
	}


	public final void putByteArray(final byte[] value) {
		currentSize += value.length;
	}


	public final void putCharArray(final char[] value) {
		currentSize += value.length * 2;
	}


	public final void putShortArray(final short[] value) {
		currentSize += value.length * 2;
	}


	public final void putIntArray(final int[] value) {
		currentSize += value.length * 4;
	}


	public final void putLongArray(final long[] value) {
		currentSize += value.length * 8;
	}


	public final void putFloatArray(final float[] value) {
		currentSize += value.length * 4;
	}


	public final void putDoubleArray(final double[] value) {
		currentSize += value.length * 8;
	}

	public final void putStringArray(String[] value) {
		int size = 0;
		for (String s : value) size += s.length() * 2 + 4;
		currentSize += size;
	}


	public final void rewind() {
		throw new UnsupportedOperationException();
	}

	public final void close() {
		//bruh
	}

	public final int pos() {
		return currentSize;
	}
}
