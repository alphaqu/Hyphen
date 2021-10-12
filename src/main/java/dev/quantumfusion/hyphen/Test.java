package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.type.Clazz;

import java.util.List;

public class Test {

	public static void main(String[] args) {
		final Clazz clx = new Clazz(Testing.class);
		for (FieldEntry f : clx.getFields()) {
			final Clazz clazz = f.clazz();
			for (FieldEntry field : clazz.getFields()) {
				System.out.println(field.clazz() + " : " + field.field().getName());
			}

			System.out.println();
			final List<FieldEntry> x = clazz.asSub(Ob1.class);
			for (FieldEntry fieldEntry : x) {
				System.out.println(fieldEntry.clazz() + " : " + fieldEntry.field().getName());
			}
			System.out.println(x);
		}
	}

	public static class Testing {
		Ob4<Param[]> ob4;
	}


	public static class Ob1<A> extends Ob2<A>{
		A field1;
	}

	public static class Ob2<A> extends Ob3<A, List<A[]>> {
		String field2;
	}

	public static class Ob3<A,B> extends Ob4<A> {
		A field3;
		B[] things;
	}

	public static class Ob4<A> {
		A field4;
	}


	public static class Param {
		boolean aBoolean;
	}
}
