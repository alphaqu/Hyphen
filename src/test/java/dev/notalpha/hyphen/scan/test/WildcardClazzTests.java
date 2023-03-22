package dev.notalpha.hyphen.scan.test;

import dev.notalpha.hyphen.scan.StructScanner;
import dev.notalpha.hyphen.scan.TestUtils;
import dev.notalpha.hyphen.scan.data.Apple;
import dev.notalpha.hyphen.scan.data.Banana;
import dev.notalpha.hyphen.scan.struct.ClassStruct;
import dev.notalpha.hyphen.scan.struct.Struct;
import dev.notalpha.hyphen.scan.struct.WildcardStruct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.List;

public class WildcardClazzTests {
	AnnotatedType getFieldType(String field) {
		try {
			return ((AnnotatedParameterizedType) this.getClass().getDeclaredField(field).getAnnotatedType()).getAnnotatedActualTypeArguments()[0];
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	List<@Apple ?> unbound;

	@Test
	void unbound() throws NoSuchFieldException {
		StructScanner clazzifier = new StructScanner();
		AnnotatedType type = getFieldType("unbound");

		Struct scan = clazzifier.scan(type, null);
		WildcardStruct expected = new WildcardStruct(List.of(TestUtils.APPLE));
		Assertions.assertEquals(expected, scan);
		Assertions.assertEquals(Object.class, scan.getBytecodeClass());
		Assertions.assertEquals(Object.class, scan.getValueClass());
	}

	List<@Apple ? extends @Banana String> upperBound;

	@Test
	void upperBound() {
		StructScanner clazzifier = new StructScanner();
		AnnotatedType type = getFieldType("upperBound");

		Struct scan = clazzifier.scan(type, null);
		Assertions.assertEquals(new WildcardStruct(
				List.of(TestUtils.APPLE),
				new ClassStruct(List.of(TestUtils.BANANA), String.class),
				false
		), scan);
		Assertions.assertEquals(String.class, scan.getBytecodeClass());
		Assertions.assertEquals(String.class, scan.getValueClass());
	}

	List<? super @Banana String> lowerBound;

	@Test
	void lowerBound() {
		StructScanner clazzifier = new StructScanner();
		AnnotatedType type = getFieldType("lowerBound");

		Struct scan = clazzifier.scan(type, null);
		Assertions.assertEquals(new WildcardStruct(new ClassStruct(List.of(TestUtils.BANANA), String.class), true), scan);
		Assertions.assertEquals(Object.class, scan.getBytecodeClass());
		Assertions.assertEquals(Object.class, scan.getValueClass());
	}
}
