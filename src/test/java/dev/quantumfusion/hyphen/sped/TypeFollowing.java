package dev.quantumfusion.hyphen.sped;

import dev.quantumfusion.hyphen.Clazzifier;
import dev.quantumfusion.hyphen.type.Clazz;
import dev.quantumfusion.hyphen.util.AnnoUtil;
import dev.quantumfusion.hyphen.util.CacheUtil;
import dev.quantumfusion.hyphen.util.Color;

import java.util.HashSet;
import java.util.Set;

import static dev.quantumfusion.hyphen.Clazzifier.UNKNOWN;

public class TypeFollowing {

	public static void main(String[] args) {
		System.out.println("hello there");

		scan(Clazzifier.create(AnnoUtil.wrap(CachingTest.Class0.class), UNKNOWN), false);
		scan(Clazzifier.create(AnnoUtil.wrap(CachingTest.Class0.class), UNKNOWN), false);
		scan(Clazzifier.create(AnnoUtil.wrap(CachingTest.Class0.class), UNKNOWN), false);
		scan(Clazzifier.create(AnnoUtil.wrap(CachingTest.Class0.class), UNKNOWN), false);

		float on = 0;
		float off = 0;

		final int iterations = 100000000;
		System.out.println("runds");
		CacheUtil.CACHE = false;
		final long l1 = System.nanoTime();
		for (int i = 0; i < iterations; i++) {
			scan(Clazzifier.create(AnnoUtil.wrap(CachingTest.Class0.class), UNKNOWN), false);
		}
		off += (System.nanoTime() - l1) / 1_000_000f;


		System.out.println("on");

		CacheUtil.CACHE = true;
		final long l = System.nanoTime();
		for (int i = 0; i < iterations; i++) {
			scan(Clazzifier.create(AnnoUtil.wrap(CachingTest.Class0.class), UNKNOWN), false);
		}
		on += (System.nanoTime() - l) / 1_000_000f;
		System.out.println("done");


		System.out.println(on + "ms / " + off + "ms");
		//CacheUtil.printCacheStatistics();
	}

	private static final Set<Clazz> SEEN = new HashSet<>();

	public static void scan(Clazz clazz, boolean print) {
		if(!SEEN.add(clazz)) return;
		final Clazz[] clazzes = Clazzifier.scanFields(clazz);
		if (print)
			System.out.println(Color.RED + clazz.toString());
		for (Clazz clazz1 : clazzes) {
			if (print)
				System.out.println("\t" + Color.CYAN + clazz1.toString() + "\t" + Color.GREEN + AnnoUtil.inlinedString(clazz1.annotations));
		}
		if (print)
			System.out.println();
		for (Clazz clazz1 : clazzes) {
			scan(clazz1, print);
		}
	}


}
