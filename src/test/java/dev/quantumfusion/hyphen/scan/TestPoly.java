package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.scan.poly.classes.*;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class TestPoly {
	C1<C0> s;
	C1<C1<C0>> s2;

	@Test
	void testC1C0() throws NoSuchFieldException {
		check("s", linkedMapOf(
				C1.class, "C1<C1:A = FieldType{C0}>",
				C2.class, "C2<C2:B = FieldType{C0}>",
				C3.class, "C3<C3:C = FieldType{C0},C3:D = FieldType{UNDEFINED}>",
				C3Def.class, "C3Def<C3Def:E = FieldType{C0}>",
				// CoWrappedC1.class,
				// CoWrappedC1Extends.class,
				// CoWrappedC1Super.class,
				C3Ignore.class, "C3Ignore<C3Ignore:C = FieldType{C0},C3Ignore:D = FieldType{UNDEFINED}>",
				C1Pair.class, "C1Pair<C1Pair:A = FieldType{C0}>",
				RecursiveC.class, "RecursiveC<RecursiveC:T = FieldType{C0}>"
		));
	}

	@Test
	void testC1C1C0() throws NoSuchFieldException {
		check("s2", linkedMapOf(
				C1.class, "C1<C1:A = FieldType{C1<C1:A = FieldType{C0}>}>",
				C2.class, "C2<C2:B = FieldType{C1<C1:A = FieldType{C0}>}>",
				C3.class, "C3<C3:C = FieldType{C1<C1:A = FieldType{C0}>},C3:D = FieldType{UNDEFINED}>",
				C3Def.class, "C3Def<C3Def:E = FieldType{C1<C1:A = FieldType{C0}>}>",
				// CoWrappedC1.class, "CoWrappedC1<CoWrappedC1:CA = FieldType{C1<C1:A = FieldType{C0}>},CoWrappedC1:C1<C1:A = FieldType{C0}>>",
				// CoWrappedC1Extends.class, "CoWrappedC1Extends<CoWrappedC1Extends:CA = FieldType{C1<C1:A = FieldType{C0}>},CoWrappedC1Extends:A = C1<C1:A = FieldType{C0}>>",
				// CoWrappedC1Super.class, "CoWrappedC1Super<CoWrappedC1Super:CA = FieldType{C1<C1:A = FieldType{C0}>},CoWrappedC1Super:A = C1<C1:A = FieldType{C0}>>",
				C3Ignore.class, "C3Ignore<C3Ignore:C = FieldType{C1<C1:A = FieldType{C0}>},C3Ignore:D = FieldType{UNDEFINED}>",
				C1Pair.class, "C1Pair<C1Pair:A = FieldType{C1<C1:A = FieldType{C0}>}>",
				RecursiveC.class, "RecursiveC<RecursiveC:T = FieldType{C1<C1:A = FieldType{C0}>}>"
		));
	}

	public static void check(String name, Map<Class<?>, String> subclasses) throws NoSuchFieldException {
		System.out.println("hello there");

		Clazz s = Clazzifier.createClass(TestPoly.class.getDeclaredField(name).getAnnotatedType().getType(), null);

		// scan(Clazzifier.createClass(CachingTest.Class0.class, null), true);

		for (var entry : subclasses.entrySet()) {
			var subclass = entry.getKey();
			var expected = entry.getValue();

			Clazz subClazz = Clazzifier.createClass(subclass, null);
			printAndSupers(subClazz);
			System.out.println("to");
			Clazz merge = (Clazz) s.map(subClazz);
			printAndSupers(merge);
			Assertions.assertEquals(expected, merge.toString());
			System.out.println();
		}
	}

	private static void printAndSupers(Clazz subClazz) {
		System.out.println(subClazz);
		Clazz aSuper = subClazz.getSuper();
		if (aSuper != null)
			printAndSupers(aSuper);
	}

	@SuppressWarnings("unchecked")
	private static <K, V> Map<K, V> linkedMapOf(Object... entries) {
		LinkedHashMap<K, V> map = new LinkedHashMap<>();

		for (int i = 0; i < entries.length; i += 2) {
			map.put((K)entries[i], (V)entries[i + 1]);
		}

		return map;
	}
}
