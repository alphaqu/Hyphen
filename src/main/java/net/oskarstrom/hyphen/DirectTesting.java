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

	public static void encode_Data(Data data, IOInterface unsafeIO) {
		unsafeIO.putString(data.integer);
		unsafeIO.putString(data.long1);
	}

	public static void encode_DataArray(DataArray data, IOInterface unsafeIO) {
		final Data[] array = data.array;
		unsafeIO.putInt(array.length);
		for (Data data1 : array) {
			encode_Data(data1, unsafeIO);
		}
	}

	public static Data decode_Data(IOInterface unsafeIO) {
		return new Data(unsafeIO.getString(), unsafeIO.getString());
	}

	public static DataArray decode_DataArray(IOInterface unsafeIO) {
		final Data[] array = new Data[unsafeIO.getInt()];
		for (int i = 0, a = array.length; i < a; i++) {
			array[i] = decode_Data(unsafeIO);
		}
		return new DataArray(array);
	}

	public static void encode_Data(Data data, UnsafeIO unsafeIO) {
		unsafeIO.putString(data.integer);
		unsafeIO.putString(data.long1);
	}

	public static void encode_DataArray(DataArray data, UnsafeIO unsafeIO) {
		final Data[] array = data.array;
		unsafeIO.putInt(array.length);
		for (Data data1 : array) {
			encode_Data(data1, unsafeIO);
		}
	}

	public static Data decode_Data(UnsafeIO unsafeIO) {
		return new Data(unsafeIO.getString(), unsafeIO.getString());
	}

	public static DataArray decode_DataArray(UnsafeIO unsafeIO) {
		final Data[] array = new Data[unsafeIO.getInt()];
		for (int i = 0, a = array.length; i < a; i++) {
			array[i] = decode_Data(unsafeIO);
		}
		return new DataArray(array);
	}

	public static void main(String[] args) {
		Data data = new Data("69", "420");

		Data[] data1 = new Data[2_500_000];
		Arrays.fill(data1, data);
		DataArray dataArray = new DataArray(data1);


		//MEASURE
		Instant measureTime = Instant.now();
		MeasureIO measure = new MeasureIO();
		encode_DataArray(dataArray, measure);
		System.out.println("Measure [" + Duration.between(measureTime, Instant.now()).toMillis() + "ms]");

		//shh
		int currentSize = measure.currentSize;
		System.out.println(Math.round(currentSize / 1_000_0f) / 100f + "MB");

		//ENCODE
		Instant encodeTime = Instant.now();
		UnsafeIO unsafeIO = UnsafeIO.create(currentSize);
		encode_DataArray(dataArray, unsafeIO);
		System.out.println("Encode [" + Duration.between(encodeTime, Instant.now()).toMillis() + "ms]");


		//DECODE
		Instant decodeTime = Instant.now();
		unsafeIO.rewind();
		DataArray decodeData = decode_DataArray(unsafeIO);
		System.out.println("Decode [" + Duration.between(decodeTime, Instant.now()).toMillis() + "ms]");




		System.out.println(dataArray.equals(decodeData));
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
