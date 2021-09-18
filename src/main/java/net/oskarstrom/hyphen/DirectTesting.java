package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.io.ByteBufferIO;
import net.oskarstrom.hyphen.io.IOInterface;
import net.oskarstrom.hyphen.io.MeasureIO;
import net.oskarstrom.hyphen.io.UnsafeIO;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

public final class DirectTesting {

	public static void measure_Data(final Data data, final MeasureIO unsafeIO) {
		unsafeIO.putString(data.integer);
		unsafeIO.putString(data.long1);
	}

	public static void encode_Data(final Data data, final IOInterface unsafeIO) {
		unsafeIO.putString(data.integer);
		unsafeIO.putString(data.long1);
	}

	public static void measure_DataArray(final DataArray data, final MeasureIO unsafeIO) {
		final Data[] array = data.array;
		unsafeIO.putInt(array.length);
		for (Data data1 : array) {
			measure_Data(data1, unsafeIO);
		}
	}


	public static void encode_DataArray(final DataArray data, final IOInterface unsafeIO) {
		final Data[] array = data.array;
		unsafeIO.putInt(array.length);
		for (Data data1 : array) {
			encode_Data(data1, unsafeIO);
		}
	}

	public static Data decode_Data(final IOInterface unsafeIO) {
		return new Data(unsafeIO.getString(), unsafeIO.getString());
	}

	public static DataArray decode_DataArray(final IOInterface unsafeIO) {
		final Data[] array = new Data[unsafeIO.getInt()];
		for (int i = 0, a = array.length; i < a; i++) {
			array[i] = decode_Data(unsafeIO);
		}
		return new DataArray(array);
	}

	public static void main(String[] args) {
		Data data = new Data("69696969696969696969696969696969696969696969696969696969696969\u263C", "420");

		Data[] data1 = new Data[500_000];
		Arrays.fill(data1, data);
		DataArray dataArray = new DataArray(data1);


		//MEASURE
		Instant measureTime = Instant.now();
		MeasureIO measure = new MeasureIO();
		measure_DataArray(dataArray, measure);
		System.out.println("Measure [" + Duration.between(measureTime, Instant.now()).toMillis() + "ms]");

		//shh
		int currentSize = measure.currentSize;
		System.out.println(Math.round(currentSize / 1_000_0f) / 100f + "MB");


		test(2, dataArray, ByteBufferIO.create(currentSize));
		System.out.println();
		test(2, dataArray, UnsafeIO.create(currentSize));

	}

	public static void test(int times, DataArray dataArray, IOInterface io) {
		//ENCODE
		Instant encodeTime = Instant.now();
		encode_DataArray(dataArray, io);
		System.out.println("Encode [" + Duration.between(encodeTime, Instant.now()).toMillis() + "ms]");
		DataArray decodeData = null;
		Instant decodeTime = Instant.now();
		for (int i = 0; i < times; i++) {
			//DECODE
			io.rewind();
			decodeData = decode_DataArray(io);
			System.out.print("=".repeat(120 / times));
		}

		System.out.println();
		System.out.println("Decode [" + Duration.between(decodeTime, Instant.now()).toMillis() / times + "ms]");
		System.out.println(decodeData.array.length);
	}

	public static final class DataArray {
		public Data[] array;

		public DataArray(Data[] array) {
			this.array = array;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof DataArray)) return false;
			DataArray dataArray = (DataArray) o;
			return Arrays.equals(array, dataArray.array);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(array);
		}
	}

	public static final class Data {
		public String integer;
		public String long1;

		public Data(String integer, String long1) {
			this.integer = integer;
			this.long1 = long1;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Data)) return false;
			Data data = (Data) o;
			return Objects.equals(integer, data.integer) && Objects.equals(long1, data.long1);
		}

		@Override
		public int hashCode() {
			return Objects.hash(integer, long1);
		}
	}
}
