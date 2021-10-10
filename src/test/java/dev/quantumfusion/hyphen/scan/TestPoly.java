package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.scan.poly.classes.*;
import dev.quantumfusion.hyphen.scan.type.Clazz;

public class TestPoly {
	C1<C0> s;

	public static void main(String[] args) throws NoSuchFieldException {
		System.out.println("hello there");

		Clazz s = Clazzifier.createClass(TestPoly.class.getDeclaredField("s").getAnnotatedType().getType(), null);

		// scan(Clazzifier.createClass(CachingTest.Class0.class, null), true);

		var subclasses = new Class<?>[]{
				C1.class,
				C2.class,
				C3.class,
				C3Def.class,
				CoWrappedC1.class,
				CoWrappedC1Extends.class,
				CoWrappedC1Super.class,
				C3Ignore.class,
				C1Pair.class,
				RecursiveC.class
		};

		for (Class<?> subclass : subclasses) {
			Clazz subClazz = Clazzifier.createClass(subclass, null);
			System.out.println(subClazz);
			System.out.println(subClazz.getSuper());
			Clazz merge = (Clazz) s.merge(subClazz);
			System.out.println(merge);
			System.out.println(merge.getSuper());
			System.out.println();
		}
	}
}
