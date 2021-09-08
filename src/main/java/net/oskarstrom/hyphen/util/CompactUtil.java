package net.oskarstrom.hyphen.util;

public class CompactUtil {
	public static byte compactBooleans(boolean... booleans) {
		byte value = 0;
		for (byte pos = 0; pos < booleans.length; pos++) {
			value |= (byte) (booleans[pos] ? Math.pow(2, pos) : 0);
		}
		return value;
	}

	public static boolean getBoolean(byte value, int pos) {
		return ((value >> pos) & 0x01) == 1;
	}
}
