package dev.notalpha.hyphen.io;

@SuppressWarnings("unused") // good morning intellij this is a library
public interface IOInterface {
	boolean getBoolean();

	byte getByte();

	char getChar();

	short getShort();

	int getInt();

	long getLong();

	float getFloat();

	double getDouble();

	String getString();

	void putBoolean(boolean value);

	void putByte(byte value);

	void putChar(char value);

	void putShort(short value);

	void putInt(int value);

	void putLong(long value);

	void putFloat(float value);

	void putDouble(double value);

	void putString(String value);

	boolean[] getBooleanArray(int length);

	byte[] getByteArray(int length);

	char[] getCharArray(int length);

	short[] getShortArray(int length);

	int[] getIntArray(int length);

	long[] getLongArray(int length);

	float[] getFloatArray(int length);

	double[] getDoubleArray(int length);

	String[] getStringArray(int length);

	void putBooleanArray(boolean[] value, int length);

	void putByteArray(byte[] value, int length);

	void putCharArray(char[] value, int length);

	void putShortArray(short[] value, int length);

	void putIntArray(int[] value, int length);

	void putLongArray(long[] value, int length);

	void putFloatArray(float[] value, int length);

	void putDoubleArray(double[] value, int length);

	void putStringArray(String[] value, int length);

	void rewind();

	int pos();

	void close();
}
