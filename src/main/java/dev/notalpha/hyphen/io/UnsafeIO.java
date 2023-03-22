package dev.notalpha.hyphen.io;


import dev.notalpha.hyphen.HyphenSerializer;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.Buffer;

/**
 * <h2>This is the créme de la créme of all IO. Highly unsafe but really fast.</h2>
 */
@SuppressWarnings({"AccessStaticViaInstance", "FinalMethodInFinalClass", "unused", "FinalStaticMethod"})
// if the jvm sees us import unsafe, it will explode:tm::tm:
public final class UnsafeIO implements IOInterface {
	private static final sun.misc.Unsafe UNSAFE = getUnsafeInstance();
	private static final int BOOLEAN_OFFSET = UNSAFE.ARRAY_BOOLEAN_BASE_OFFSET;
	private static final int BYTE_OFFSET = UNSAFE.ARRAY_BYTE_BASE_OFFSET;
	private static final int CHAR_OFFSET = UNSAFE.ARRAY_CHAR_BASE_OFFSET;
	private static final int SHORT_OFFSET = UNSAFE.ARRAY_SHORT_BASE_OFFSET;
	private static final int INT__OFFSET = UNSAFE.ARRAY_INT_BASE_OFFSET;
	private static final int LONG_OFFSET = UNSAFE.ARRAY_LONG_BASE_OFFSET;
	private static final int FLOAT_OFFSET = UNSAFE.ARRAY_FLOAT_BASE_OFFSET;
	private static final int DOUBLE_OFFSET = UNSAFE.ARRAY_DOUBLE_BASE_OFFSET;
	private static final long STRING_FIELD_OFFSET;
	private static final long STRING_ENCODING_OFFSET;
	private static final long BUFFER_ADDRESS_OFFSET;
	//If an array is below this value it will just use the regular methods. Else it will use memcpy
	private static final int COPY_MEMORY_THRESHOLD = 10;

	static {
		try {
			STRING_FIELD_OFFSET = UNSAFE.objectFieldOffset(String.class.getDeclaredField("value"));
			STRING_ENCODING_OFFSET = UNSAFE.objectFieldOffset(String.class.getDeclaredField("coder"));
			BUFFER_ADDRESS_OFFSET = UNSAFE.objectFieldOffset(Buffer.class.getDeclaredField("address"));
		} catch (NoSuchFieldException e) {
			throw new RuntimeException();
		}
	}

	@Nullable
	private final Buffer bb;
	private final long address;
	private long currentAddress;

	private UnsafeIO(final long address, @Nullable Buffer bb) {
		this.address = address;
		this.currentAddress = address;
		this.bb = bb;
	}

	private static sun.misc.Unsafe getUnsafeInstance() {
		Class<sun.misc.Unsafe> clazz = sun.misc.Unsafe.class;
		for (Field field : clazz.getDeclaredFields()) {
			if (!field.getType().equals(clazz)) {
				continue;
			}
			final int modifiers = field.getModifiers();
			if (!(Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers))) {
				continue;
			}
			try {
				field.setAccessible(true);
				return (sun.misc.Unsafe) field.get(null);
			} catch (Exception ignored) {
			}
			break;
		}

		throw new IllegalStateException("Unsafe is unavailable.");
	}

	public static final UnsafeIO create(final int size) {
		return new UnsafeIO(UNSAFE.allocateMemory(size), null);
	}

	public static final UnsafeIO wrap(final Buffer directByteBuffer) {
		if (!directByteBuffer.isDirect()) {
			throw new IllegalArgumentException("Bytebuffer is not direct");
		}
		return new UnsafeIO(UNSAFE.getLong(directByteBuffer, BUFFER_ADDRESS_OFFSET), directByteBuffer);
	}

	public static final <O> UnsafeIO create(final HyphenSerializer<UnsafeIO, O> serializer, final O data) {
		return create((int) serializer.measure(data));
	}

	// ======================================= FUNC ======================================= //
	@Override
	public final void rewind() {
		currentAddress = address;
	}

	@Override
	public final int pos() {
		return (int) ((int) currentAddress - address);
	}

	@Override
	public final void close() {
		if (bb != null) {
			bb.clear();
		} else {
			UNSAFE.freeMemory(address);
		}
	}

	public long address() {
		return address;
	}

	// ======================================== GET ======================================== //
	@Override
	public final boolean getBoolean() {
		return UNSAFE.getBoolean(null, currentAddress++);
	}

	@Override
	public final byte getByte() {
		return UNSAFE.getByte(null, currentAddress++);
	}

	@Override
	public final char getChar() {
		final char c = UNSAFE.getChar(null, currentAddress);
		currentAddress += 2;
		return c;
	}

	@Override
	public final short getShort() {
		final short s = UNSAFE.getShort(null, currentAddress);
		currentAddress += 2;
		return s;
	}

	@Override
	public final int getInt() {
		final int i = UNSAFE.getInt(null, currentAddress);
		currentAddress += 4;
		return i;
	}

	@Override
	public final long getLong() {
		final long l = UNSAFE.getLong(null, currentAddress);
		currentAddress += 8;
		return l;
	}

	@Override
	public final float getFloat() {
		final float f = UNSAFE.getFloat(null, currentAddress);
		currentAddress += 4;
		return f;
	}

	@Override
	public final double getDouble() {
		final double f = UNSAFE.getDouble(null, currentAddress);
		currentAddress += 8;
		return f;
	}

	@Override
	public final String getString() {
		try {
			final var infoBytes = UNSAFE.getInt(null, currentAddress);
			if (infoBytes == 0) {
				currentAddress += 4;
				return "";
			}
			final var string = (String) UNSAFE.allocateInstance(String.class);
			final var byteArray = new byte[Math.abs(infoBytes) /*length*/];
			final var arrayLength = byteArray.length;
			UNSAFE.copyMemory(null, currentAddress + 4, byteArray, BYTE_OFFSET, arrayLength);
			UNSAFE.putObject(string, STRING_FIELD_OFFSET, byteArray);
			UNSAFE.putByte(string, STRING_ENCODING_OFFSET, (byte) (infoBytes < 0 ? 1 : 0));
			currentAddress += arrayLength + 4;
			return string;
		} catch (InstantiationException e) {
			throw new RuntimeException("String creation failed: ", e);
		}
	}


	// ======================================== PUT ======================================== //
	@Override
	public final void putBoolean(final boolean value) {
		UNSAFE.putBoolean(null, currentAddress++, value);
	}

	@Override
	public final void putByte(final byte value) {
		UNSAFE.putByte(null, currentAddress++, value);
	}

	@Override
	public final void putChar(final char value) {
		UNSAFE.putChar(null, currentAddress, value);
		currentAddress += 2;
	}

	@Override
	public final void putShort(final short value) {
		UNSAFE.putShort(null, currentAddress, value);
		currentAddress += 2;
	}

	@Override
	public final void putInt(final int value) {
		UNSAFE.putInt(null, currentAddress, value);
		currentAddress += 4;
	}

	@Override
	public final void putLong(final long value) {
		UNSAFE.putLong(null, currentAddress, value);
		currentAddress += 8;
	}

	@Override
	public final void putFloat(final float value) {
		UNSAFE.putFloat(null, currentAddress, value);
		currentAddress += 4;
	}

	@Override
	public final void putDouble(final double value) {
		UNSAFE.putDouble(null, currentAddress, value);
		currentAddress += 8;
	}

	@Override
	public final void putString(final String value) {
		final byte[] bytes = (byte[]) UNSAFE.getObject(value, STRING_FIELD_OFFSET);
		final int length = bytes.length;
		UNSAFE.putInt(null, currentAddress, UNSAFE.getByte(value, STRING_ENCODING_OFFSET) == 0 ? length : -length);
		UNSAFE.copyMemory(bytes, BYTE_OFFSET, null, currentAddress + 4, length);
		currentAddress += length + 4;
	}


	// ====================================== GET_ARR ======================================== //
	@Override
	public final boolean[] getBooleanArray(final int length) {
		final boolean[] array = new boolean[length];
		if (length > COPY_MEMORY_THRESHOLD) {
			UNSAFE.copyMemory(null, currentAddress, array, BOOLEAN_OFFSET, length);
			currentAddress += length;
		} else {
			for (int i = 0; i < length; i++) {
				array[i] = getBoolean();
			}
		}
		return array;
	}

	@Override
	public final byte[] getByteArray(final int length) {
		final byte[] array = new byte[length];
		if (length > COPY_MEMORY_THRESHOLD) {
			UNSAFE.copyMemory(null, currentAddress, array, BYTE_OFFSET, length);
			currentAddress += length;
		} else {
			for (int i = 0; i < length; i++) {
				array[i] = getByte();
			}
		}
		return array;
	}

	@Override
	public final char[] getCharArray(final int length) {
		final char[] array = new char[length];
		if (length > COPY_MEMORY_THRESHOLD) {
			final int bytes = length * 2;
			UNSAFE.copyMemory(null, currentAddress, array, CHAR_OFFSET, bytes);
			currentAddress += bytes;
		} else {
			for (int i = 0; i < length; i++) {
				array[i] = getChar();
			}
		}
		return array;
	}

	@Override
	public final short[] getShortArray(final int length) {
		final short[] array = new short[length];
		if (length > COPY_MEMORY_THRESHOLD) {
			final int bytes = length * 2;
			UNSAFE.copyMemory(null, currentAddress, array, SHORT_OFFSET, bytes);
			currentAddress += bytes;
		} else {
			for (int i = 0; i < length; i++) {
				array[i] = getShort();
			}
		}
		return array;
	}

	@Override
	public final int[] getIntArray(final int length) {
		final int[] array = new int[length];
		if (length > COPY_MEMORY_THRESHOLD) {
			final int bytes = length * 4;
			UNSAFE.copyMemory(null, currentAddress, array, INT__OFFSET, bytes);
			currentAddress += bytes;
		} else {
			for (int i = 0; i < length; i++) {
				array[i] = getInt();
			}
		}
		return array;
	}

	@Override
	public final long[] getLongArray(final int length) {
		final long[] array = new long[length];
		if (length > COPY_MEMORY_THRESHOLD) {
			final int bytes = length * 8;
			UNSAFE.copyMemory(null, currentAddress, array, LONG_OFFSET, bytes);
			currentAddress += bytes;
		} else {
			for (int i = 0; i < length; i++) {
				array[i] = getLong();
			}
		}
		return array;
	}

	@Override
	public final float[] getFloatArray(final int length) {
		final float[] array = new float[length];
		if (length > COPY_MEMORY_THRESHOLD) {
			final int bytes = length * 4;
			UNSAFE.copyMemory(null, currentAddress, array, FLOAT_OFFSET, bytes);
			currentAddress += bytes;
		} else {
			for (int i = 0; i < length; i++) {
				array[i] = getFloat();
			}
		}
		return array;
	}


	@Override
	public final double[] getDoubleArray(final int length) {
		final double[] array = new double[length];
		if (length > COPY_MEMORY_THRESHOLD) {
			final int bytes = length * 8;
			UNSAFE.copyMemory(null, currentAddress, array, DOUBLE_OFFSET, bytes);
			currentAddress += bytes;
		} else {
			for (int i = 0; i < length; i++) {
				array[i] = getDouble();
			}
		}
		return array;
	}

	@Override
	public final String[] getStringArray(final int length) {
		final String[] array = new String[length];
		for (int i = 0; i < length; i++) {
			array[i] = getString();
		}
		return array;
	}

	// ====================================== PUT_ARR ======================================== //
	@Override
	public final void putBooleanArray(final boolean[] value, final int length) {
		if (length > COPY_MEMORY_THRESHOLD) {
			UNSAFE.copyMemory(value, BOOLEAN_OFFSET, null, currentAddress, length);
			currentAddress += length;
		} else {
			for (var v : value) {
				putBoolean(v);
			}
		}
	}

	@Override
	public final void putByteArray(final byte[] value, final int length) {
		if (length > COPY_MEMORY_THRESHOLD) {
			UNSAFE.copyMemory(value, BYTE_OFFSET, null, currentAddress, length);
			currentAddress += length;
		} else {
			for (var v : value) {
				putByte(v);
			}
		}
	}

	@Override
	public final void putCharArray(final char[] value, final int length) {
		if (length > COPY_MEMORY_THRESHOLD) {
			final int bytes = length * 2;
			UNSAFE.copyMemory(value, CHAR_OFFSET, null, currentAddress, bytes);
			currentAddress += bytes;
		} else {
			for (var v : value) {
				putChar(v);
			}
		}
	}

	@Override
	public final void putShortArray(final short[] value, final int length) {
		if (length > COPY_MEMORY_THRESHOLD) {
			final int bytes = length * 2;
			UNSAFE.copyMemory(value, SHORT_OFFSET, null, currentAddress, bytes);
			currentAddress += bytes;
		} else {
			for (var v : value) {
				putShort(v);
			}
		}
	}

	@Override
	public final void putIntArray(final int[] value, final int length) {
		if (length > COPY_MEMORY_THRESHOLD) {
			final int bytes = length * 4;
			UNSAFE.copyMemory(value, INT__OFFSET, null, currentAddress, bytes);
			currentAddress += bytes;
		} else {
			for (var v : value) {
				putInt(v);
			}
		}
	}

	@Override
	public final void putLongArray(final long[] value, final int length) {
		if (length > COPY_MEMORY_THRESHOLD) {
			final int bytes = length * 8;
			UNSAFE.copyMemory(value, LONG_OFFSET, null, currentAddress, bytes);
			currentAddress += bytes;
		} else {
			for (var v : value) {
				putLong(v);
			}
		}
	}

	@Override
	public final void putFloatArray(final float[] value, final int length) {
		if (length > COPY_MEMORY_THRESHOLD) {
			final int bytes = length * 4;
			UNSAFE.copyMemory(value, FLOAT_OFFSET, null, currentAddress, bytes);
			currentAddress += bytes;
		} else {
			for (var v : value) {
				putFloat(v);
			}
		}
	}

	@Override
	public final void putDoubleArray(final double[] value, final int length) {
		if (length > COPY_MEMORY_THRESHOLD) {
			final int bytes = length * 8;
			UNSAFE.copyMemory(value, DOUBLE_OFFSET, null, currentAddress, bytes);
			currentAddress += bytes;
		} else {
			for (var v : value) {
				putDouble(v);
			}
		}
	}

	@Override
	public final void putStringArray(final String[] value, final int length) {
		for (int i = 0; i < length; i++) {
			putString(value[i]);
		}
	}

	public static final int getStringBytes(String string) {
		return (((byte[]) UNSAFE.getObject(string, STRING_FIELD_OFFSET)).length) + 4 /* the array length */;
	}
}
