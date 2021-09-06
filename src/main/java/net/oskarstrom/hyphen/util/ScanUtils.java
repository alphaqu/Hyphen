package net.oskarstrom.hyphen.util;

import net.oskarstrom.hyphen.data.ClassInfo;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ScanUtils {


	/**
	 * <h2>Searches in superclasses and interfaces, and then creates the final object.</h2>
	 *
	 * @param classInfo The Class to scan
	 * @param matcher   If this returns true it will return the result
	 * @param creator   When found use the creator to create the object
	 * @param <O>       Return object
	 * @return The found result made with the creator, else null.
	 */
	public static <O> O search(ClassInfo classInfo, Function<ClassInfo, Boolean> matcher, Function<ClassInfo, O> creator) {
		@Nullable ClassInfo[] classInfos = searchPath(classInfo, matcher, 0, true);
		if (classInfos != null) {
			return creator.apply(classInfos[classInfos.length - 1]);
		}
		return null;
	}


	@Nullable
	public static ClassInfo[] searchPath(ClassInfo classInfo, Function<ClassInfo, Boolean> matcher, int depth, boolean mapTypes) {
		final ClassInfo[] mappedSubclasses = classInfo.getSuperAndInterfaces(mapTypes);
		if (mappedSubclasses != null) {
			for (ClassInfo info : mappedSubclasses) {
				if (matcher.apply(info)) {
					ClassInfo[] classInfos = new ClassInfo[depth + 1];
					classInfos[depth] = info;
					return classInfos;
				}
			}

			//if no implementations scan deeper
			for (ClassInfo info : mappedSubclasses) {
				ClassInfo[] impl = searchPath(info, matcher, depth + 1,mapTypes);
				if (impl != null) {
					impl[depth] = info;
					return impl;
				}
			}
		}

		return null;
	}
}
