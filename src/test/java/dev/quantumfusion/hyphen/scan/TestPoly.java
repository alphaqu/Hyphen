package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.scan.poly.classes.c.*;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TestPoly {
	C1<C0> s;

	@Test
	void testC1C0() throws NoSuchFieldException {
		check("s", Map.of(
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

	public static void check(String name, Map<Class<?>, String> subclasses) throws NoSuchFieldException {
		System.out.println("hello there");


	}

	private static void printAndSupers(Clazz subClazz) {
		System.out.println(subClazz);
	}
}
