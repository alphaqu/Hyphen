package dev.quantumfusion.hyphen.util;

@SuppressWarnings("FinalMethodInFinalClass")
public final class MeasureHandler {
	public int currentSize = 0;
	
	public final void measureBoolean() {
		currentSize++;
	}

	public final void measureByte() {
		currentSize++;
	}
	
	public final void measureChar() {
		currentSize += 2;
	}


	public final void measureShort() {
		currentSize += 2;
	}


	public final void measureInt() {
		currentSize += 4;
	}


	public final void measureLong() {
		currentSize += 8;
	}


	public final void measureFloat() {
		currentSize += 4;
	}


	public final void measureDouble() {
		currentSize += 8;
	}

	public final void measureString(String value) {
		currentSize += value.length() * 2 + 4;
	}
	
	public final void measureBooleanArray(final boolean[] value) {
		currentSize += value.length;
	}


	public final void measureByteArray(final byte[] value) {
		currentSize += value.length;
	}


	public final void measureCharArray(final char[] value) {
		currentSize += value.length * 2;
	}


	public final void measureShortArray(final short[] value) {
		currentSize += value.length * 2;
	}


	public final void measureIntArray(final int[] value) {
		currentSize += value.length * 4;
	}


	public final void measureLongArray(final long[] value) {
		currentSize += value.length * 8;
	}


	public final void measureFloatArray(final float[] value) {
		currentSize += value.length * 4;
	}


	public final void measureDoubleArray(final double[] value) {
		currentSize += value.length * 8;
	}

	public final void measureStringArray(String[] value) {
		int size = 0;
		for (String s : value) size += s.length() * 2 + 4;
		currentSize += size;
	}
	

	public final int getSize() {
		return currentSize;
	}
}
