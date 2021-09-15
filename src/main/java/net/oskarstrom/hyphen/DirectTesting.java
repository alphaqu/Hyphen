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

	//ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQC9We/OPEZueBLnRFLA8zFL6sNZyzujY0BMRD6JdI+8oouZY8Z40jZCn0TZNMjN1KsguCc4Hapml0wOY3tT3O0C1mO6ILc44e631HVuqNvwpKQ0gWPq6rKCTiCgUAitFpLlMRe44fSRlQmWLUdaXJuaaolWBVCNtvrxuoy10xHykf9csUhThgbvS1fKe0sTpG4QbodanwTRjGRRfBc1svBtqcCtGGeFT1tt5geQP7t+It9IvpRdF5pgD47Xnyeu0m57SZRttPbqwDRk+GhlDmFcXlccSCu2BzcUdLldYll8hQvUqZDmurbxjMb4eXRIthJv+VAQCGN6d7MHIotVoFP/zVzM74KoWb9PFpbBTtn6YZwYvQnT6tnLRYxCUp51OWd3tyychCHrUGEc5hD88Se+mfqaL/Z9m/8yE89XcfrT5PrEoAW4eFeTcnlgY9R5KH91zh/thIOvaGBbotvGsjns3moLtz1phn+wqwZqR6mw3/0gSOL1/D9QMejOnha+UsaEUg6O6lmcPYfX7xIYwPApM8h7I1oiEvn6f3szBEoTvhrsoY6QztvezBX+1COe4zfM7wTy/lRPhbEnYdwAXJMqvV3D+7FKNlRL4ohUFKw+mzvvsa8642zYuMa7D4ZHGwoBcgNX1zdVuLeMlIHdYddj56YnthmWD7rB5YXcDgplKw== skynet\yanchu06121@SE12149456
	//============================= C2-compiled nmethod ==============================
	//----------------------------------- Assembly -----------------------------------
	//
	//Compiled method (c2)    4930  164             net.oskarstrom.hyphen.DirectTesting::decode_DataArray (42 bytes)
	// total in heap  [0x000002adb74a9d10,0x000002adb74aa598] = 2184
	// relocation     [0x000002adb74a9e70,0x000002adb74a9eb8] = 72
	// main code      [0x000002adb74a9ec0,0x000002adb74aa200] = 832
	// stub code      [0x000002adb74aa200,0x000002adb74aa238] = 56
	// oops           [0x000002adb74aa238,0x000002adb74aa240] = 8
	// metadata       [0x000002adb74aa240,0x000002adb74aa260] = 32
	// scopes data    [0x000002adb74aa260,0x000002adb74aa310] = 176
	// scopes pcs     [0x000002adb74aa310,0x000002adb74aa520] = 528
	// dependencies   [0x000002adb74aa520,0x000002adb74aa528] = 8
	// handler table  [0x000002adb74aa528,0x000002adb74aa588] = 96
	// nul chk table  [0x000002adb74aa588,0x000002adb74aa598] = 16
	//
	//[Constant Pool (empty)]
	//
	//[MachCode]
	//[Verified Entry Point]
	//  # {method} {0x000002adbf803eb8} 'decode_DataArray' '(Lnet/oskarstrom/hyphen/io/IOInterface;)Lnet/oskarstrom/hyphen/DirectTesting$DataArray;' in 'net/oskarstrom/hyphen/DirectTesting'
	//  # parm0:    rdx:rdx   = 'net/oskarstrom/hyphen/io/IOInterface'
	//  #           [sp+0x70]  (sp of caller)
	//  0x000002adb74a9ec0: 8984 2400 | 90ff ff55
	//
	//  0x000002adb74a9ec8: ;*synchronization entry
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@-1 (line 47)
	//  0x000002adb74a9ec8: 4883 ec60 | 4c8b d244 | 8b5a 0866 | 0f1f 8400 | 0000 0000 | 6666 6690
	//
	//  0x000002adb74a9ee0: ;   {metadata('net/oskarstrom/hyphen/io/ByteBufferIO')}
	//  0x000002adb74a9ee0: 4181 fb00 | 0e18 000f | 85a5 0200 | 004c 8954 | 2420 498b
	//
	//  0x000002adb74a9ef4: ;   {optimized virtual_call}
	//  0x000002adb74a9ef4: d266 90e8
	//
	//  0x000002adb74a9ef8: ; ImmutableOopMap {[32]=Oop }
	//                      ;*invokeinterface getInt {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@1 (line 47)
	//  0x000002adb74a9ef8: 842e f8ff | 8be8 6690 | 81f8 0000 | 0400 0f87 | 7e02 0000
	//
	//  0x000002adb74a9f0c: ;*anewarray {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@6 (line 47)
	//  0x000002adb74a9f0c: 4863 c881 | f800 0004 | 000f 873e
	//
	//  0x000002adb74a9f18: ;*aastore {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@26 (line 49)
	//  0x000002adb74a9f18: 0200 004d | 8b87 3001 | 0000 48c1 | e102 4883
	//
	//  0x000002adb74a9f28: ;*anewarray {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@6 (line 47)
	//  0x000002adb74a9f28: c117 4c8b | d149 83e2 | f84d 8bd8 | 4d03 da66 | 0f1f 8400 | 0000 0000 | 4d3b 9f40 | 0100 000f
	//  0x000002adb74a9f48: 830c 0200
	//
	//  0x000002adb74a9f4c: ;*aastore {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@26 (line 49)
	//  0x000002adb74a9f4c: 004d 899f | 301 0000 | 498b f848 | 83c7 1049 | c700 0100 | 0000 410f | 0d8b c000
	//
	//  0x000002adb74a9f68: ;   {metadata('net/oskarstrom/hyphen/DirectTesting$Data'[])}
	//
	//  0x000002adb74a9fb4: ;*anewarray {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@6 (line 47)
	//  0x000002adb74a9fb4: 48aa 4c89 | 4424 3844
	//
	//  0x000002adb74a9fbc: ;*arraylength {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@13 (line 48)
	//  0x000002adb74a9fbc: 8bd5 6690 | 4585 d20f | 8e13 0100
	//
	//  0x000002adb74a9fc8: ;*if_icmpge {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@17 (line 48)
	//  0x000002adb74a9fc8: 0033 edeb | 2249 8bd7 | 49ba 1063 | 496f fb7f | 0000 41ff
	//
	//  0x000002adb74a9fdc: ;*aastore {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@26 (line 49)
	//  0x000002adb74a9fdc: d266 6690 | 3b6c 2430 | 0f8d f200
	//
	//  0x000002adb74a9fe8: ;*if_icmpge {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@17 (line 48)
	//  0x000002adb74a9fe8: 0000 448b | 5424 3044
	//
	//  0x000002adb74a9ff0: ;*aload_1 {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@20 (line 49)
	//  0x000002adb74a9ff0: 8954 2430 | 488b 5424
	//
	//  0x000002adb74a9ff8: ;   {static_call}
	//  0x000002adb74a9ff8: 2066 90e8
	//
	//  0x000002adb74a9ffc: ; ImmutableOopMap {[32]=Oop [56]=Oop }
	//                      ;*invokestatic decode_Data {reexecute=0 rethrow=0 return_oop=1}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@23 (line 49)
	//  0x000002adb74a9ffc: 8033 f8ff | 488b d83b | 6c24 300f | 8327 0100 | 004c 8b54 | 2438 498d | 7caa 1066 | 0f1f 8400
	//  0x000002adb74aa01c: 0000 0000 | 4180 7f30 | 000f 857a
	//
	//  0x000002adb74aa028: ;*aastore {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@26 (line 49)
	//  0x000002adb74aa028: 0000 004c
	//
	//  0x000002adb74aa02c: ;*iinc {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@27 (line 48)
	//  0x000002adb74aa02c: 8bd3 ffc5 | 4c8b db44 | 891f 4c8b | df4d 33d3 | 49c1 ea14 | 4d85 d274 | 9b48 85db | 7496 49c1
	//  0x000002adb74aa04c: eb09 48b9 | 0000 46ba | ad02 0000 | 4903 cb0f | 1f44 0000 | 8039 040f | 8477 ffff | ff4d 8b57
	//  0x000002adb74aa06c: 784d 8b5f | 68f0 8344 | 24c0 0080 | 3900 660f | 1f44 0000 | 0f84 5aff | ffff 4488 | 214d 85db
	//  0x000002adb74aa08c: 0f84 3bff | ffff 4b89 | 4c1a f849 | 83c3 f84d | 895f 6890 | e93b ffff | ff44 8b17 | 4585 d20f
	//  0x000002adb74aa0ac: 847a ffff | ff4d 8b5f | 3849 8bca | 4d85 db0f | 1f44 0000 | 0f84 7e00 | 0000 4d8b | 5748 4b89
	//  0x000002adb74aa0cc: 4c1a f849 | 83c3 f84d | 895f 38e9 | 4fff ffff | 498b 8730 | 0100 004c | 8bd0 4983 | c210 4d3b
	//  0x000002adb74aa0ec: 9740 0100 | 000f 837d
	//
	//  0x000002adb74aa0f4: ;*aastore {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@26 (line 49)
	//  0x000002adb74aa0f4: 0000 004d | 8997 3001 | 0000 410f | 0d8a c000 | 0000 48c7 | 0001 0000
	//
	//  0x000002adb74aa10c: ;   {metadata('net/oskarstrom/hyphen/DirectTesting$DataArray')}
	//  0x000002adb74aa10c: 00c7 4008 | 7f02 1800
	//
	//  0x000002adb74aa114: ;*new {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@33 (line 51)
	//  0x000002adb74aa114: 4489 600c | 4c8b 5424 | 3844 8950
	//
	//  0x000002adb74aa120: ;*synchronization entry
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@-1 (line 47)
	//  0x000002adb74aa120: 0c48 83c4
	//
	//  0x000002adb74aa124: ;   {poll_return}
	//  0x000002adb74aa124: 605d 493b | a718 0100 | 000f 8799 | 0000 00c3 | bae4 ffff | ff48 8944
	//
	//  0x000002adb74aa13c: ;   {runtime_call UncommonTrapBlob}
	//  0x000002adb74aa13c: 2440 90e8
	//
	//  0x000002adb74aa140: ; ImmutableOopMap {[32]=Oop [56]=Oop [64]=Oop }
	//                      ;*aastore {reexecute=1 rethrow=0 return_oop=0}
	//                      ; - (reexecute) net.oskarstrom.hyphen.DirectTesting::decode_DataArray@26 (line 49)
	//  0x000002adb74aa140: 3c35 f8ff | 498b d749 | ba30 6349 | 6ffb 7f00
	//
	//  0x000002adb74aa150: ;*aastore {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@26 (line 49)
	//  0x000002adb74aa150: 0041 ffd2 | e9d2 feff
	//
	//  0x000002adb74aa158: ;   {metadata('net/oskarstrom/hyphen/DirectTesting$Data'[])}
	//  0x000002adb74aa158: ff48 ba48 | 1bc0 0008 | 0000 0044
	//
	//  0x000002adb74aa164: ;   {runtime_call _new_array_Java}
	//  0x000002adb74aa164: 8bc0 90e8
	//
	//  0x000002adb74aa168: ; ImmutableOopMap {[32]=Oop }
	//                      ;*anewarray {reexecute=0 rethrow=0 return_oop=1}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@6 (line 47)
	//  0x000002adb74aa168: 94af f5ff | 4c8b c0e9
	//
	//  0x000002adb74aa170: ;*if_icmpge {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@17 (line 48)
	//  0x000002adb74aa170: 42fe ffff
	//
	//  0x000002adb74aa174: ;   {metadata('net/oskarstrom/hyphen/DirectTesting$DataArray')}
	//  0x000002adb74aa174: 48ba f813 | c000 0800 | 0000 488b
	//
	//  0x000002adb74aa180: ;   {runtime_call _new_instance_Java}
	//  0x000002adb74aa180: 6c24 38e8
	//
	//  0x000002adb74aa184: ; ImmutableOopMap {rbp=Oop [56]=Oop }
	//                      ;*new {reexecute=0 rethrow=0 return_oop=1}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@33 (line 51)
	//  0x000002adb74aa184: 78a2 f5ff | eb8e 4863
	//
	//  0x000002adb74aa18c: ;*anewarray {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@6 (line 47)
	//  0x000002adb74aa18c: c8e9 7dfd | ffff bade | ffff ff4c | 8954 2420
	//
	//  0x000002adb74aa19c: ;   {runtime_call UncommonTrapBlob}
	//  0x000002adb74aa19c: 6666 90e8
	//
	//  0x000002adb74aa1a0: ; ImmutableOopMap {[32]=Oop }
	//                      ;*invokeinterface getInt {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@1 (line 47)
	//  0x000002adb74aa1a0: dc34 f8ff | baf6 ffff
	//
	//  0x000002adb74aa1a8: ;   {runtime_call UncommonTrapBlob}
	//  0x000002adb74aa1a8: ff66 90e8
	//
	//  0x000002adb74aa1ac: ; ImmutableOopMap {}
	//                      ;*invokeinterface getInt {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@1 (line 47)
	//  0x000002adb74aa1ac: d034 f8ff | 488b d0eb
	//
	//  0x000002adb74aa1b4: ;*anewarray {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@6 (line 47)
	//
	//  0x000002adb74aa1b8: ;*invokeinterface getInt {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@1 (line 47)
	//
	//  0x000002adb74aa1bc: ;*invokestatic decode_Data {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@23 (line 49)
	//
	//  0x000002adb74aa1c0: ;*anewarray {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@6 (line 47)
	//
	//  0x000002adb74aa1c4: ;   {runtime_call _rethrow_Java}
	//
	//  0x000002adb74aa1c8: ;*aastore {reexecute=0 rethrow=0 return_oop=0}
	//                      ; - net.oskarstrom.hyphen.DirectTesting::decode_DataArray@26 (line 49)
	//
	//  0x000002adb74aa1cc: ;   {internal_word}
	//
	//  0x000002adb74aa1dc: ;   {runtime_call SafepointBlob}
	//[Stub Code]
	//  0x000002adb74aa200: ;   {no_reloc}
	//
	//  0x000002adb74aa208: ;   {runtime_call}
	//
	//  0x000002adb74aa20c: ;   {static_stub}
	//
	//  0x000002adb74aa218: ;   {runtime_call}
	//
	//  0x000002adb74aa21c: ;   {runtime_call ExceptionBlob}
	//
	//  0x000002adb74aa22c: ;   {runtime_call DeoptimizationBlob}
}
