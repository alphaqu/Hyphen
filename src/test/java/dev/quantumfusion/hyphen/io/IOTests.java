package dev.quantumfusion.hyphen.io;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class IOTests {
	private static final boolean[] BOOLEANS = {true, false, true, true, false, false};
	private static final byte[] BYTES = {0, Byte.MIN_VALUE, Byte.MAX_VALUE};

	private static final short[] SHORTS = {0, Short.MIN_VALUE, Short.MAX_VALUE};
	private static final char[] CHARS = {0, Character.MIN_VALUE, Character.MAX_VALUE, '\uD83E', '\uDD80'};

	private static final int[] INTS = {0, Integer.MIN_VALUE, Integer.MAX_VALUE};
	private static final long[] LONGS = {0, Long.MIN_VALUE, Long.MAX_VALUE};

	private static final float[] FLOATS = {0, Float.MIN_VALUE, Float.MAX_VALUE, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NaN, Float.MIN_NORMAL};
	private static final double[] DOUBLES = {0, Double.MIN_VALUE, Double.MAX_VALUE, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN, Double.MIN_NORMAL};

	private static final String[] STRING = {"", "glosco", "ඞ", "Ayo, Crashnite?"};

	private static final Object[] ARRAYS = {BOOLEANS, BYTES, SHORTS, CHARS, INTS, LONGS, FLOATS, DOUBLES, STRING};
	private static final int SIZE;

	private static final Map<Class<?>, Function<IOInterface, Object>> GET_ARRAY_FUNC = new HashMap<>();
	private static final Map<Class<?>, Function<IOInterface, Object>> GET_FUNC = new HashMap<>();
	private static final Map<Class<?>, BiConsumer<Object, IOInterface>> PUT_ARRAY_FUNC = new HashMap<>();
	private static final Map<Class<?>, BiConsumer<Object, IOInterface>> PUT_FUNC = new HashMap<>();

	public static final int TEST_SIZE = 1000000;

	static {
		int size = 0;
		for (Object array : ARRAYS) {
			final int arrayBytes = getSize(array);
			size += (arrayBytes * 2) + 4; // two array runs and the length byte for one of them
		}
		SIZE = size;


		GET_ARRAY_FUNC.put(boolean[].class, IOInterface::getBooleanArray);
		GET_ARRAY_FUNC.put(byte[].class, IOInterface::getByteArray);
		GET_ARRAY_FUNC.put(short[].class, IOInterface::getShortArray);
		GET_ARRAY_FUNC.put(char[].class, IOInterface::getCharArray);
		GET_ARRAY_FUNC.put(int[].class, IOInterface::getIntArray);
		GET_ARRAY_FUNC.put(float[].class, IOInterface::getFloatArray);
		GET_ARRAY_FUNC.put(long[].class, IOInterface::getLongArray);
		GET_ARRAY_FUNC.put(double[].class, IOInterface::getDoubleArray);
		GET_ARRAY_FUNC.put(String[].class, IOInterface::getStringArray);

		GET_FUNC.put(boolean.class, IOInterface::getBoolean);
		GET_FUNC.put(byte.class, IOInterface::getByte);
		GET_FUNC.put(short.class, IOInterface::getShort);
		GET_FUNC.put(char.class, IOInterface::getChar);
		GET_FUNC.put(int.class, IOInterface::getInt);
		GET_FUNC.put(float.class, IOInterface::getFloat);
		GET_FUNC.put(long.class, IOInterface::getLong);
		GET_FUNC.put(double.class, IOInterface::getDouble);
		GET_FUNC.put(String.class, IOInterface::getString);

		PUT_ARRAY_FUNC.put(boolean[].class, (o, io) -> io.putBooleanArray((boolean[]) o));
		PUT_ARRAY_FUNC.put(byte[].class, (o, io) -> io.putByteArray((byte[]) o));
		PUT_ARRAY_FUNC.put(short[].class, (o, io) -> io.putShortArray((short[]) o));
		PUT_ARRAY_FUNC.put(char[].class, (o, io) -> io.putCharArray((char[]) o));
		PUT_ARRAY_FUNC.put(int[].class, (o, io) -> io.putIntArray((int[]) o));
		PUT_ARRAY_FUNC.put(float[].class, (o, io) -> io.putFloatArray((float[]) o));
		PUT_ARRAY_FUNC.put(long[].class, (o, io) -> io.putLongArray((long[]) o));
		PUT_ARRAY_FUNC.put(double[].class, (o, io) -> io.putDoubleArray((double[]) o));
		PUT_ARRAY_FUNC.put(String[].class, (o, io) -> io.putStringArray((String[]) o));

		PUT_FUNC.put(boolean.class, (o, io) -> io.putBoolean((boolean) o));
		PUT_FUNC.put(byte.class, (o, io) -> io.putByte((byte) o));
		PUT_FUNC.put(short.class, (o, io) -> io.putShort((short) o));
		PUT_FUNC.put(char.class, (o, io) -> io.putChar((char) o));
		PUT_FUNC.put(int.class, (o, io) -> io.putInt((int) o));
		PUT_FUNC.put(float.class, (o, io) -> io.putFloat((float) o));
		PUT_FUNC.put(long.class, (o, io) -> io.putLong((long) o));
		PUT_FUNC.put(double.class, (o, io) -> io.putDouble((double) o));
		PUT_FUNC.put(String.class, (o, io) -> io.putString((String) o));
	}

	@Test
	void unsafe() {
		testIO(UnsafeIO::create, TEST_SIZE);
	}

	@Test
	void unsafeWrapped() {
		testIO(size -> UnsafeIO.wrap(ByteBuffer.allocateDirect(size)), TEST_SIZE);
	}

	@Test
	void directBytebuffer() {
		testIO(ByteBufferIO::createDirect, TEST_SIZE);
	}

	@Test
	void heapBytebuffer() {
		testIO(ByteBufferIO::create, TEST_SIZE);
	}

	@Test
	void array() {
		testIO(ArrayIO::create, TEST_SIZE);
	}

	private static <IO extends IOInterface> void testIO(Function<Integer, IO> ioCreator, int size) {
		final Object[] arrays;
		if (size > 1) {
			arrays = new Object[ARRAYS.length * size];
			for (int i = 0; i < ARRAYS.length * size; i++)
				arrays[i] = ARRAYS[i % ARRAYS.length];
		} else {
			arrays = ARRAYS;
		}


		final IO io = ioCreator.apply(SIZE * size);
		for (Object array : arrays) {
			testArrayPut(io, array);
		}
		io.rewind();
		for (Object array : arrays) {
			testArrayGet(io, array);
		}
		io.close();
	}

	private static <IO extends IOInterface> void testArrayPut(IO io, Object array) {
		PUT_ARRAY_FUNC.get(array.getClass()).accept(array, io);
		var put = PUT_FUNC.get(array.getClass().getComponentType());
		iterate(array, o -> put.accept(o, io));
	}

	private static <IO extends IOInterface> void testArrayGet(IO io, Object arrayRaw) {
		Object result = GET_ARRAY_FUNC.get(arrayRaw.getClass()).apply(io);
		var get = GET_FUNC.get(arrayRaw.getClass().getComponentType());
		iterate(arrayRaw, o -> {
			final Object apply = get.apply(io);
			if (!o.equals(apply)) {
				throw new RuntimeException(arrayRaw.getClass().getComponentType() + " Array Value does not match. " + o + " != " + apply);
			}
		});
		if (!arraysMatch(arrayRaw, result)) {
			throw new RuntimeException("opsie arr");
		}
	}

	public static int getSize(Object array) {
		if (array instanceof boolean[] i) return i.length;
		else if (array instanceof byte[] i) return i.length;
		else if (array instanceof short[] i) return i.length * 2;
		else if (array instanceof char[] i) return i.length * 2;
		else if (array instanceof int[] i) return i.length * 4;
		else if (array instanceof float[] i) return i.length * 4;
		else if (array instanceof long[] i) return i.length * 8;
		else if (array instanceof double[] i) return i.length * 8;
		else if (array instanceof String[] i) {
			return (i.length * 16) + 16;
		}
		throw new RuntimeException("what");
	}

	public static boolean arraysMatch(Object array, Object array2) {
		if (array instanceof boolean[] i) return Arrays.equals(i, (boolean[]) array2);
		else if (array instanceof byte[] i) return Arrays.equals(i, (byte[]) array2);
		else if (array instanceof short[] i) return Arrays.equals(i, (short[]) array2);
		else if (array instanceof char[] i) return Arrays.equals(i, (char[]) array2);
		else if (array instanceof int[] i) return Arrays.equals(i, (int[]) array2);
		else if (array instanceof float[] i) return Arrays.equals(i, (float[]) array2);
		else if (array instanceof long[] i) return Arrays.equals(i, (long[]) array2);
		else if (array instanceof double[] i) return Arrays.equals(i, (double[]) array2);
		else if (array instanceof String[] i) return Arrays.equals(i, (String[]) array2);
		throw new RuntimeException("what");
	}

	public static <A extends Number> void iterate(Object array, Consumer arrayConsumer) {
		if (array instanceof boolean[] i) {
			for (boolean b : i) arrayConsumer.accept(b);
			return;
		} else if (array instanceof byte[] i) {
			for (byte b : i) arrayConsumer.accept(b);
			return;
		} else if (array instanceof short[] i) {
			for (short value : i) arrayConsumer.accept(value);
			return;
		} else if (array instanceof char[] i) {
			for (char c : i) arrayConsumer.accept(c);
			return;
		} else if (array instanceof int[] i) {
			for (int j : i) arrayConsumer.accept(j);
			return;
		} else if (array instanceof float[] i) {
			for (float v : i) arrayConsumer.accept(v);
			return;
		} else if (array instanceof long[] i) {
			for (long l : i) arrayConsumer.accept(l);
			return;
		} else if (array instanceof double[] i) {
			for (double v : i) arrayConsumer.accept(v);
			return;
		} else if (array instanceof String[] i) {
			for (String v : i) arrayConsumer.accept(v);
			return;
		}
		throw new RuntimeException("what");
	}
}
