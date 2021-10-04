package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.type.Clazz;
import dev.quantumfusion.hyphen.util.AnnoUtil;
import dev.quantumfusion.hyphen.util.CacheUtil;
import dev.quantumfusion.hyphen.util.Color;

import static dev.quantumfusion.hyphen.Clazzifier.UNKNOWN;

public class TypeFollowing {

	public static void main(String[] args) {
		System.out.println("hello there");
		final long l = System.nanoTime();
		scan(Clazzifier.create(AnnoUtil.wrap(ForwardTest.class), UNKNOWN), true);
		System.out.println((System.nanoTime() - l) / 1_000_000f + "ms");
		CacheUtil.printCacheStatistics();
	}

	public static void scan(Clazz clazz, boolean print) {
		final Clazz[] clazzes = Clazzifier.scanFields(clazz);
		if (print)
			System.out.println(Color.RED + clazz.pullClass().getSimpleName());
		for (Clazz clazz1 : clazzes) {
			if (print)
				System.out.println("\t" + Color.CYAN + clazz1.toString() + "\t" + Color.GREEN + AnnoUtil.inlinedString(clazz1.annotations) + Color.YELLOW + AnnoUtil.inlinedString(clazz1.globalAnnotations));
		}
		if (print)
			System.out.println();
		for (Clazz clazz1 : clazzes) {
			scan(clazz1, print);
		}
	}


}
