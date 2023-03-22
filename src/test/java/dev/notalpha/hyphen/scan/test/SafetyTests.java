package dev.notalpha.hyphen.scan.test;

import dev.notalpha.hyphen.scan.StructField;
import dev.notalpha.hyphen.scan.StructScanner;
import dev.notalpha.hyphen.scan.struct.*;
import dev.notalpha.hyphen.thr.UnknownTypeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.constant.Constable;

public class SafetyTests {

	public static class UpperLimit<T extends Number & Constable> {

	}


	UpperLimit<Integer> wildcardBoundLimit;


	@Test
	void wildcardBoundLimit() {
		StructScanner clazzifier = new StructScanner();

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			clazzifier.scan(getClass().getDeclaredField("wildcardBoundLimit").getAnnotatedType(), null);
		});
	}

	public static class Unresolvable<T> {
		T value;
	}


	@Test
	void unresolvableType() throws NoSuchFieldException {
		StructScanner clazzifier = new StructScanner();

		Struct value = clazzifier.scan(Unresolvable.class.getDeclaredField("value").getAnnotatedType(), null);

		Assertions.assertThrows(UnknownTypeException.class, () -> value.getBytecodeClass());
		Assertions.assertThrows(UnknownTypeException.class, () -> value.getValueClass());
	}

	@Test
	void hashcodeWorks() throws NoSuchFieldException {
		new ClassStruct(int.class).hashCode();
		ParameterStruct parameterStruct = new ParameterStruct(new ClassStruct(int.class), new ClassStruct(int.class), "");
		parameterStruct.hashCode();
		new TypeStruct(parameterStruct).hashCode();
		new WildcardStruct().hashCode();
		new StructField(Unresolvable.class.getDeclaredField("value"), new ClassStruct(int.class)).hashCode();
	}

	@Test
	void unsupportedResolving() {
		Assertions.assertThrows(UnsupportedOperationException.class,
				() -> new WildcardStruct().extendType(null)
		);
	}
}
