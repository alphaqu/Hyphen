package dev.quantumfusion.hyphen.sped;

import dev.quantumfusion.hyphen.Clazzifier;
import dev.quantumfusion.hyphen.type.AnnType;
import dev.quantumfusion.hyphen.type.Clazz;
import dev.quantumfusion.hyphen.util.AnnoUtil;
import dev.quantumfusion.hyphen.util.CacheUtil;
import dev.quantumfusion.hyphen.util.Color;

import java.util.HashSet;
import java.util.Set;

public class TypeFollowing {

	public static void main(String[] args) {
		System.out.println("hello there");

		scan(Clazzifier.createClass(CachingTest.Class0.class, null), false);
		scan(Clazzifier.createClass(CachingTest.Class0.class, null), false);
		scan(Clazzifier.createClass(CachingTest.Class0.class, null), false);
		scan(Clazzifier.createClass(CachingTest.Class0.class, null), false);

		float on = 0;
		float off = 0;

		final int iterations = 1_000_000;
		CacheUtil.CACHE = false;
		final long l1 = System.nanoTime();
		for (int i = 0; i < iterations; i++) {
			scan(Clazzifier.createClass(CachingTest.Class0.class, null), false);
		}
		off += (System.nanoTime() - l1) / 1_000_000f;


		System.out.println("on");

		CacheUtil.CACHE = true;
		final long l = System.nanoTime();
		for (int i = 0; i < iterations; i++) {
			scan(Clazzifier.createClass(CachingTest.Class0.class, null), false);
		}
		on += (System.nanoTime() - l) / 1_000_000f;
		System.out.println("done");


		System.out.println(on + "ms / " + off + "ms");
		//CacheUtil.printCacheStatistics();
	}

	private static final Set<Clazz> SEEN = new HashSet<>();

	public static void scan(Clazz clazz, boolean print) {
		if(!SEEN.add(clazz)) return;
		final AnnType[] fields = Clazzifier.scanFields(clazz);
		if (print)
			System.out.println(Color.RED + clazz.toString());
		for (var field : fields) {
			if (print)
				System.out.println("\t" + Color.CYAN + field.clazz().toString() + "\t" + Color.GREEN + AnnoUtil.inlinedString(field.annotations()) + "\t" + Color.YELLOW + AnnoUtil.inlinedString(field.globalAnnotations()));
		}
		if (print)
			System.out.println();
		for (var field : fields) {
			if(field.clazz() instanceof Clazz clazz1)
				scan(clazz1, print);
		}
	}
}
