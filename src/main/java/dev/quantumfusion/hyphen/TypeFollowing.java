package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.type.Clazz;
import dev.quantumfusion.hyphen.util.CacheUtil;

import static dev.quantumfusion.hyphen.Clazzifier.UNKNOWN;

public class TypeFollowing {

	public static void main(String[] args) {
		System.out.println("hello there");
		final Clazz type = Clazzifier.create(dev.quantumfusion.hyphen.Subclasses.class, UNKNOWN);
		final long l = System.nanoTime();
		final Clazz[] clazzes = Clazzifier.scanFields(type);
		for (Clazz clazz : clazzes) {
			System.out.println(clazz);
			final Clazz sub = clazz.getSub(dev.quantumfusion.hyphen.Subclasses.Subclass.class);
			scan(sub, true);
		}
		System.out.println((System.nanoTime() - l) / 1_000_000f + "ms");
		CacheUtil.printCacheStatistics();
	}

	public static void scan(Clazz clazz, boolean print) {
		final Clazz[] clazzes = Clazzifier.scanFields(clazz);
		if (print)
			System.out.println(clazz.pullClass().getSimpleName());
		for (Clazz clazz1 : clazzes) {
			if (print)
				System.out.println("\t" + clazz1.toString());
		}
		if (print)
			System.out.println();
		for (Clazz clazz1 : clazzes) {
			scan(clazz1, print);
		}
	}


}
