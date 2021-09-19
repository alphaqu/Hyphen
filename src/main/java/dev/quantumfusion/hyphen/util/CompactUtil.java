package dev.quantumfusion.hyphen.util;

public class CompactUtil {
	/**
	 * <h3>(b1 << 0)</h3>
	 * <h3>(b1 << 0, b2 << 1)</h3>
	 * <h3>(b1 << 0, b2 << 1, b3 << 2)</h3>
	 * <h3>(b1 << 0, b2 << 1, b3 << 2, b4 << 3)</h3>
	 */
	public static byte compactBooleans(boolean... booleans) {
		byte value = 0;
		for (byte pos = 0; pos < booleans.length; pos++) {
			value |= (byte) (booleans[pos] ? 1 << pos : 0);
		}
		return value;
	}

	public static boolean getBoolean(byte value, int pos) {
		return ((value >> pos) & 0x01) == 1;
	}
}
