package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.type.AnnType;
import dev.quantumfusion.hyphen.type.Clazz;
import dev.quantumfusion.hyphen.util.AnnoUtil;
import dev.quantumfusion.hyphen.util.Color;

import java.util.HashSet;
import java.util.Set;

public class TypeFollowing {

	public static void main(String[] args) {
		System.out.println("hello there");
		final long l = System.nanoTime();
		scan(Clazzifier.createClass(ForwardTest.class, null), true);
		System.out.println((System.nanoTime() - l) / 1_000_000f + "ms");
		//CacheUtil.printCacheStatistics();
	}

	private static final Set<Clazz> SEEN = new HashSet<>();

	public static void scan(Clazz clazz, boolean print) {
		if(!SEEN.add(clazz) || clazz.toString().length() > 500) return;
		final AnnType[] fields = Clazzifier.scanFields(clazz);
		if (print)
			System.out.println(Color.RED + clazz.toString());
		for (var field : fields) {
			if (print)
				System.out.println("\t" + Color.CYAN + field.clazz().toString() + "\t" + Color.GREEN + AnnoUtil.inlinedString(field.annotations()) + Color.YELLOW + AnnoUtil.inlinedString(field.globalAnnotations()));
		}
		if (print)
			System.out.println();
		for (var field : fields) {
			if(field.clazz() instanceof Clazz clazz1)
				scan(clazz1, print);
		}
	}


}
