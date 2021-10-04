package dev.quantumfusion.hyphen;

import java.util.List;

public class Subclasses {
	SuperClass<List<Integer>, ?> superClass;

	public static class Subclass<A> extends Subclass2<A, List<String>> {
		A[] thing;
	}

	public static class Subclass2<A, B> extends Subclass3<A> {
		A[] thing;
		B b;
	}

	public static class Subclass3<A> extends SuperClass<A, List<String>> {
		int integer;
	}

	public static class SuperClass<A, B> {
		A thing;
		B thing2;
	}
}
