package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.util.Color;

public class PrintNicefier {

	public static void main(String[] args) {
		String c2 = """
				============================= C2-compiled nmethod ==============================
				----------------------------------- Assembly -----------------------------------
				    
				Compiled method (c2)    8531  483   !   4       net.oskarstrom.hyphen.io.UnsafeIO::getString (134 bytes)
				 total in heap  [0x00000129d0dffe10,0x00000129d0e00738] = 2344
				 relocation     [0x00000129d0dfff70,0x00000129d0dfffa0] = 48
				 main code      [0x00000129d0dfffa0,0x00000129d0e00300] = 864
				 stub code      [0x00000129d0e00300,0x00000129d0e00318] = 24
				 oops           [0x00000129d0e00318,0x00000129d0e00328] = 16
				 metadata       [0x00000129d0e00328,0x00000129d0e00388] = 96
				 scopes data    [0x00000129d0e00388,0x00000129d0e004c0] = 312
				 scopes pcs     [0x00000129d0e004c0,0x00000129d0e00700] = 576
				 dependencies   [0x00000129d0e00700,0x00000129d0e00708] = 8
				 handler table  [0x00000129d0e00708,0x00000129d0e00738] = 48
				    
				[Constant Pool (empty)]
				    
				[MachCode]
				[Entry Point]
				  # {method} {0x00000129dd41bfa8} 'getString' '()Ljava/lang/String;' in 'net/oskarstrom/hyphen/io/UnsafeIO'
				  #           [sp+0x80]  (sp of caller)
				  0x00000129d0dfffa0: 448b 5208 | 49c1 e203 | 49bb 0000 | 0000 0800 | 0000 4d03 | d349 3bc2\s
				    
				  0x00000129d0dfffb8: ;   {runtime_call ic_miss_stub}
				  0x00000129d0dfffb8: 0f85 c2ca | 53f8 6690\s
				[Verified Entry Point]
				  0x00000129d0dfffc0: 8984 2400 | 90ff ff55\s
				    
				  0x00000129d0dfffc8: ;*synchronization entry
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@-1 (line 89)
				  0x00000129d0dfffc8: 4883 ec70 | 4889 5424 | 204c 8b52\s
				    
				  0x00000129d0dfffd4: ;*getfield currentAddress {reexecute=0 rethrow=0 return_oop=0}
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@5 (line 89)
				  0x00000129d0dfffd4: 1849 8bea | 4d8b da45\s
				    
				  0x00000129d0dfffdc: ;*invokevirtual getInt {reexecute=0 rethrow=0 return_oop=0}
				                      ; - sun.misc.Unsafe::getInt@5 (line 164)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@13 (line 89)
				  0x00000129d0dfffdc: 8b73 1090 | 4585 f60f | 846a 0200 | 0049 8bb7 | 3001 0000 | 4c8b de49 | 83c3 1866 | 0f1f 8400\s
				  0x00000129d0dffffc: 0000 0000 | 4d3b 9f40 | 0100 000f | 83d5 0100\s
				    
				  0x00000129d0e0000c: ;*invokevirtual copyMemory0 {reexecute=0 rethrow=0 return_oop=0}
				                      ; - jdk.internal.misc.Unsafe::copyMemory@29 (line 806)
				                      ; - sun.misc.Unsafe::copyMemory@11 (line 573)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@72 (line 95)
				  0x00000129d0e0000c: 004d 899f | 3001 0000 | 410f 0d8b | c000 0000 | 48c7 0601\s
				    
				  0x00000129d0e00020: ;   {metadata('java/lang/String')}
				  0x00000129d0e00020: 0000 00c7 | 4608 3b3b | 0000 4489 | 660c 4c89\s
				    
				  0x00000129d0e00030: ;*invokevirtual allocateInstance {reexecute=0 rethrow=0 return_oop=0}
				                      ; - sun.misc.Unsafe::allocateInstance@4 (line 892)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@29 (line 92)
				  0x00000129d0e00030: 6610 458b | de41 c1fb | 1f41 8bde | 4133 db41\s
				    
				  0x00000129d0e00040: ;*invokestatic abs {reexecute=0 rethrow=0 return_oop=0}
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@37 (line 93)
				  0x00000129d0e00040: 2bdb 81fb | 0000 1000 | 0f87 fd01 | 0000 4863\s
				    
				  0x00000129d0e00050: ;*newarray {reexecute=0 rethrow=0 return_oop=0}
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@40 (line 93)
				  0x00000129d0e00050: cb81 fb00 | 0010 000f | 87ab 0100\s
				    
				  0x00000129d0e0005c: ;*invokevirtual copyMemory0 {reexecute=0 rethrow=0 return_oop=0}
				                      ; - jdk.internal.misc.Unsafe::copyMemory@29 (line 806)
				                      ; - sun.misc.Unsafe::copyMemory@11 (line 573)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@72 (line 95)
				  0x00000129d0e0005c: 004d 8baf | 3001 0000\s
				    
				  0x00000129d0e00064: ;*newarray {reexecute=0 rethrow=0 return_oop=0}
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@40 (line 93)
				  0x00000129d0e00064: 4883 c117 | 4c8b d949 | 83e3 f84d | 8bc5 4d03 | c366 6666 | 0f1f 8400 | 0000 0000 | 4d3b 8740\s
				  0x00000129d0e00084: 0100 000f | 837b 0100\s
				    
				  0x00000129d0e0008c: ;*invokevirtual copyMemory0 {reexecute=0 rethrow=0 return_oop=0}
				                      ; - jdk.internal.misc.Unsafe::copyMemory@29 (line 806)
				                      ; - sun.misc.Unsafe::copyMemory@11 (line 573)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@72 (line 95)
				  0x00000129d0e0008c: 004d 8987 | 3001 0000 | 410f 0d88 | c000 0000 | 49c7 4500 | 0100 0000 | 410f 0d88 | 0001 0000\s
				  0x00000129d0e000ac: ;   {metadata({type array byte})}
				  0x00000129d0e000ac: 41c7 4508 | 112b 0000 | 4189 5d0c | 410f 0d88 | 4001 0000 | 498b fd48 | 83c7 1041 | 0f0d 8880\s
				  0x00000129d0e000cc: 0100 0048 | c1e9 0348 | 83c1 fe48 | 33c0 4883 | f908 7f10 | 48ff c978 | 1248 8904 | cf48 ffc9\s
				  0x00000129d0e000ec: 7df7 eb07 | 48c1 e103\s
				    
				  0x00000129d0e000f4: ;*arraylength {reexecute=0 rethrow=0 return_oop=0}
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@44 (line 94)
				  0x00000129d0e000f4: f348 aa4c\s
				    
				  0x00000129d0e000f8: ;*i2l {reexecute=0 rethrow=0 return_oop=0}
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@71 (line 95)
				  0x00000129d0e000f8: 63c3 85db | 0f1f 4000 | 0f84 6201 | 0000 498b | d548 83c2 | 1041 c687 | a403 0000\s
				    
				  0x00000129d0e00114: ;*invokevirtual copyMemory0 {reexecute=0 rethrow=0 return_oop=0}
				                      ; - jdk.internal.misc.Unsafe::copyMemory@29 (line 806)
				                      ; - sun.misc.Unsafe::copyMemory@11 (line 573)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@72 (line 95)
				  0x00000129d0e00114: 0148 8bcd | 4883 c114 | 0f1f 4000 | 49ba e0c9 | 32c9 2901 | 0000 41ff | d24c 8bd6 | 4983 c214\s
				  0x00000129d0e00134: 4588 a7a4\s
				    
				  0x00000129d0e00138: ;*invokevirtual copyMemory0 {reexecute=0 rethrow=0 return_oop=0}
				                      ; - jdk.internal.misc.Unsafe::copyMemory@29 (line 806)
				                      ; - sun.misc.Unsafe::copyMemory@11 (line 573)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@72 (line 95)
				  0x00000129d0e00138: 0300 0083 | c304 4863 | fb45 33db | 4d8b c24d | 8bcd 4589 | 0a4d 8bd5 | 4d33 d049 | c1ea 14bb\s
				  0x00000129d0e00158: 0100 0000 | 4585 f641 | 0f4d db4d | 85d2 7416 | 49c1 e809 | 48b9 0000 | edd7 2901 | 0000 4903\s
				  0x00000129d0e00178: c880 3904\s
				    
				  0x00000129d0e0017c: ;*invokevirtual putReference {reexecute=0 rethrow=0 return_oop=0}
				                      ; - sun.misc.Unsafe::putObject@7 (line 213)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@83 (line 96)
				  0x00000129d0e0017c: 7522 885e\s
				    
				  0x00000129d0e00180: ;*invokevirtual putByte {reexecute=0 rethrow=0 return_oop=0}
				                      ; - sun.misc.Unsafe::putByte@7 (line 237)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@103 (line 97)
				  0x00000129d0e00180: 104c 8b54 | 2420 4901\s
				    
				  0x00000129d0e00188: ;*synchronization entry
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@-1 (line 89)
				  0x00000129d0e00188: 7a18 488b | c648 83c4\s
				    
				  0x00000129d0e00190: ;   {poll_return}
				  0x00000129d0e00190: 705d 493b | a718 0100 | 000f 8749 | 0100 00c3 | 4d8b 5768 | 4d8b 5f78 | f083 4424 | c000 8039\s
				  0x00000129d0e001b0: 0074 cb44 | 8821 4d85\s
				    
				  0x00000129d0e001b8: ;*invokevirtual copyMemory0 {reexecute=0 rethrow=0 return_oop=0}
				                      ; - jdk.internal.misc.Unsafe::copyMemory@29 (line 806)
				                      ; - sun.misc.Unsafe::copyMemory@11 (line 573)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@72 (line 95)
				  0x00000129d0e001b8: d275 1449 | 8bd7 6690 | 49ba 1063 | 496f fb7f | 0000 41ff | d2eb af4b\s
				    
				  0x00000129d0e001d0: ;*invokevirtual putReference {reexecute=0 rethrow=0 return_oop=0}
				                      ; - sun.misc.Unsafe::putObject@7 (line 213)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@83 (line 96)
				  0x00000129d0e001d0: 894c 13f8\s
				    
				  0x00000129d0e001d4: ;*invokevirtual copyMemory0 {reexecute=0 rethrow=0 return_oop=0}
				                      ; - jdk.internal.misc.Unsafe::copyMemory@29 (line 806)
				                      ; - sun.misc.Unsafe::copyMemory@11 (line 573)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@72 (line 95)
				  0x00000129d0e001d4: 4983 c2f8\s
				    
				  0x00000129d0e001d8: ;*invokevirtual putReference {reexecute=0 rethrow=0 return_oop=0}
				                      ; - sun.misc.Unsafe::putObject@7 (line 213)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@83 (line 96)
				  0x00000129d0e001d8: 4d89 5768 | 0f1f 4000 | eb9c 4489\s
				    
				  0x00000129d0e001e4: ;   {metadata('java/lang/String')}
				  0x00000129d0e001e4: 7424 3048 | bad8 d901 | 0008 0000\s
				    
				  0x00000129d0e001f0: ;   {runtime_call _new_instance_Java}
				  0x00000129d0e001f0: 0066 90e8\s
				    
				  0x00000129d0e001f4: ; ImmutableOopMap {[32]=Oop }
				                      ;*invokevirtual allocateInstance {reexecute=0 rethrow=0 return_oop=1}
				                      ; - sun.misc.Unsafe::allocateInstance@4 (line 892)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@29 (line 92)
				  0x00000129d0e001f4: 8836 51f8 | 4c8b d544 | 8b74 2430 | 488b f0e9 | 2afe ffff | 895c 2434 | 4889 7424 | 3844 8974\s
				  0x00000129d0e00214: 2430 4c89\s
				    
				  0x00000129d0e00218: ;*invokevirtual allocateInstance {reexecute=0 rethrow=0 return_oop=0}
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@29 (line 92)
				                      ;   {metadata({type array byte})}
				  0x00000129d0e00218: 5424 2848 | ba88 5801 | 0008 0000 | 0044 8bc3\s
				    
				  0x00000129d0e00228: ;   {runtime_call _new_array_Java}
				  0x00000129d0e00228: 6666 90e8\s
				    
				  0x00000129d0e0022c: ; ImmutableOopMap {[32]=Oop [56]=Oop }
				                      ;*newarray {reexecute=0 rethrow=0 return_oop=1}
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@40 (line 93)
				  0x00000129d0e0022c: d031 5ff8 | 4c8b 5424 | 2844 8b74 | 2430 488b | 7424 388b | 5c24 344c | 8be8 e9ac | feff ff48\s
				  0x00000129d0e0024c: ;*newarray {reexecute=0 rethrow=0 return_oop=0}
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@40 (line 93)
				  0x00000129d0e0024c: 63cb e9fe | fdff ffba | 45ff ffff | 488b 6c24 | 2044 8974\s
				    
				  0x00000129d0e00260: ;   {runtime_call UncommonTrapBlob}
				  0x00000129d0e00260: 2424 90e8\s
				    
				  0x00000129d0e00264: ; ImmutableOopMap {rbp=Oop }
				                      ;*ifne {reexecute=1 rethrow=0 return_oop=0}
				                      ; - (reexecute) net.oskarstrom.hyphen.io.UnsafeIO::getString@18 (line 90)
				  0x00000129d0e00264: 18d4 53f8\s
				    
				  0x00000129d0e00268: ;*ladd {reexecute=0 rethrow=0 return_oop=0}
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@63 (line 95)
				  0x00000129d0e00268: 4983 c214 | 4533 db4d | 3bc3 bdff | ffff ff7c | 0840 0f95 | c540 0fb6\s
				    
				  0x00000129d0e00280: ;*lcmp {reexecute=0 rethrow=0 return_oop=0}
				                      ; - jdk.internal.misc.Unsafe::checkSize@22 (line 468)
				                      ; - jdk.internal.misc.Unsafe::copyMemoryChecks@3 (line 831)
				                      ; - jdk.internal.misc.Unsafe::copyMemory@9 (line 800)
				                      ; - sun.misc.Unsafe::copyMemory@11 (line 573)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@72 (line 95)
				  0x00000129d0e00280: edba 45ff | ffff 4489 | 7424 284c | 8954 2430 | 4889 7424 | 3889 5c24 | 2c4c 896c | 2448 4c89\s
				  0x00000129d0e002a0: ;   {runtime_call UncommonTrapBlob}
				  0x00000129d0e002a0: 4424 50e8\s
				    
				  0x00000129d0e002a4: ; ImmutableOopMap {[32]=Oop [56]=Oop [72]=Oop }
				                      ;*ifne {reexecute=1 rethrow=0 return_oop=0}
				                      ; - (reexecute) jdk.internal.misc.Unsafe::copyMemory@16 (line 802)
				                      ; - sun.misc.Unsafe::copyMemory@11 (line 573)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@72 (line 95)
				  0x00000129d0e002a4: d8d3 53f8 | 448b 5008\s
				    
				  0x00000129d0e002ac: ;   {metadata('java/lang/InstantiationException')}
				  0x00000129d0e002ac: 4181 fa92 | a318 0074 | 2148 8bd0 | eb12 448b | 5008 6690\s
				    
				  0x00000129d0e002c0: ;   {metadata('java/lang/InstantiationException')}
				  0x00000129d0e002c0: 4181 fa92 | a318 0074\s
				    
				  0x00000129d0e002c8: ;*invokevirtual allocateInstance {reexecute=0 rethrow=0 return_oop=0}
				                      ; - sun.misc.Unsafe::allocateInstance@4 (line 892)
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@29 (line 92)
				  0x00000129d0e002c8: 1248 8bd0 | 4883 c470\s
				    
				  0x00000129d0e002d0: ;   {runtime_call _rethrow_Java}
				  0x00000129d0e002d0: 5de9 2a5c | 5ff8 488b\s
				    
				  0x00000129d0e002d8: ;*newarray {reexecute=0 rethrow=0 return_oop=0}
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@40 (line 93)
				  0x00000129d0e002d8: e8eb 0348\s
				    
				  0x00000129d0e002dc: ;*areturn {reexecute=0 rethrow=0 return_oop=0}
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@121 (line 99)
				  0x00000129d0e002dc: 8be8 ba7e\s
				    
				  0x00000129d0e002e0: ;   {runtime_call UncommonTrapBlob}
				  0x00000129d0e002e0: 0000 00e8\s
				    
				  0x00000129d0e002e4: ; ImmutableOopMap {rbp=Oop }
				                      ;*new {reexecute=0 rethrow=0 return_oop=0}
				                      ; - net.oskarstrom.hyphen.io.UnsafeIO::getString@123 (line 101)
				  0x00000129d0e002e4: 98d3 53f8\s
				    
				  0x00000129d0e002e8: ;   {internal_word}
				  0x00000129d0e002e8: 49ba 9201 | e0d0 2901 | 0000 4d89 | 9790 0300\s
				    
				  0x00000129d0e002f8: ;   {runtime_call SafepointBlob}
				  0x00000129d0e002f8: 00e9 02e0 | 53f8 f4f4\s
				[Exception Handler]
				  0x00000129d0e00300: ;   {no_reloc}
				  0x00000129d0e00300: e97b 4451 | f8e8 0000 | 0000 4883\s
				    
				  0x00000129d0e0030c: ;   {runtime_call DeoptimizationBlob}
				  0x00000129d0e0030c: 2c24 05e9 | 0cd7 53f8 | f4f4 f4f4\s
				[/MachCode]
				========================================================================================================================""";

		StringBuilder out = new StringBuilder();
		for (String s : c2.split("\n")) {
			if (!(s.endsWith("\s") || s.contains(";   {"))) {
				s = s.replace(";", Color.RED + ";");
				s = s.replace("::", Color.RED + "::" + Color.GREEN);
				s = s.replace("-", Color.CYAN + "-");
				s = s.replace("*", Color.YELLOW + "*");
				s = s.replace("@", Color.WHITE + "@");
				out.append(s).append('\n').append(Color.WHITE);
			}
		}
		System.out.println(out);
	}
}
