package net.oskarstrom.hyphen.data;

import net.oskarstrom.hyphen.util.ScanUtils;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * List(ClassInfo[]) diffrent impl
 * ClassInfo[] ArrayList > AbstractList > List
 */
public class SubclassMap extends LinkedHashMap<Class<?>, SubclassMap.SubclassEntry[]> {

	public void addSubclasses(Class<?> interfaceOrSuper, Class<?>[] subclasses) {
		SubclassMap.SubclassEntry[] subclassEntries = new SubclassEntry[subclasses.length];
		for (int i = 0, subclassesSize = subclasses.length; i < subclassesSize; i++) {
			ClassInfo subclass = new ClassInfo(subclasses[i], null);
			@Nullable ClassInfo[] path = ScanUtils.searchPath(subclass, ci -> ci.clazz == interfaceOrSuper, 0, false);
			if (path == null) {
				throw new RuntimeException(subclass.clazz.getSimpleName() + " does not inherit " + interfaceOrSuper.getSimpleName());
			}
			subclassEntries[i] = new SubclassEntry(subclass, path);
		}
		put(interfaceOrSuper, subclassEntries);
	}

	@Nullable
	public ClassInfo[] mapSubclasses(ClassInfo info) {
		Class<?> interfaceOrSuper = info.getClazz();
		if (containsKey(interfaceOrSuper)) {
			SubclassMap.SubclassEntry[] implementations = get(interfaceOrSuper);
			int size = implementations.length;

			ClassInfo[] mappedImplementations = new ClassInfo[size];


			for (int j = 0; j < size; j++) {
				SubclassMap.SubclassEntry impl = implementations[j];

				TypeMap implTypeMap = info.typeMap;
				ClassInfo[] pathToTarget = impl.pathToTarget;
				for (int i = pathToTarget.length - 1; i >= 0; i--) {
					implTypeMap = implTypeMap.mapClassInfo(pathToTarget[i]);
				}
				ClassInfo implInfo = impl.implementation;

				mappedImplementations[j] = new ClassInfo(implInfo.clazz, implTypeMap, implInfo.genericType);
			}


			return mappedImplementations;
		}
		return null;
	}

	public record SubclassEntry(ClassInfo implementation, ClassInfo[] pathToTarget) {
	}

}
