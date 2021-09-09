package net.oskarstrom.hyphen.io;

/**
 * Should only by used as a referance. Never implement it as it will hurt performance and make the jvm not inline.
 */
public interface IOInterface {
	boolean getBoolean();
	byte getByte();
	char getChar();
	short getShort();
	int getInt();
	long getLong();
	float getFloat();
	double getDouble();
	boolean[] getBooleanArray(int length);
	byte[] getByteArray(int length);
	char[] getCharArray(int length);
	short[] getShortArray(int length);
	int[] getIntArray(int length);
	long[] getLongArray(int length);
	float[] getFloatArray(int length);
	double[] getDoubleArray(int length);
	IOInterface putBoolean(boolean value);
	IOInterface putByte(byte value);
	IOInterface putChar(char value);
	IOInterface putShort(short value);
	IOInterface putInt(int value);
	IOInterface putLong(long value);
	IOInterface putFloat(float value);
	IOInterface putDouble(double value);
	IOInterface putBooleanArray(boolean[] value);
	IOInterface putByteArray(byte[] value);
	IOInterface putCharArray(char[] value);
	IOInterface putShortArray(short[] value);
	IOInterface putIntArray(int[] value);
	IOInterface putLongArray(long[] value);
	IOInterface putFloatArray(float[] value);
	IOInterface putDoubleArray(double[] value);
	void rewind();
	int pos();
}
