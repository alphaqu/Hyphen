package dev.quantumfusion.hyphen.scan.test;

import dev.quantumfusion.hyphen.scan.StructScanner;
import dev.quantumfusion.hyphen.scan.struct.ClassStruct;
import dev.quantumfusion.hyphen.scan.struct.ParameterStruct;
import dev.quantumfusion.hyphen.scan.struct.Struct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AdvancedTests {

	public static class Value<O, V extends List<O>> {

	}

	Value<?, List<Integer>> type;

	@Test
	void basic() throws NoSuchFieldException {
		StructScanner clazzifier = new StructScanner();
		Struct clazz = clazzifier.scan(this.getClass().getDeclaredField("type").getAnnotatedType(), null);
		System.out.println(clazz.simpleString());
		Assertions.assertEquals(
				new ClassStruct(List.of(), Value.class, List.of(
						new ParameterStruct(
								ClassStruct.OBJECT,
								"O"
						),
						new ParameterStruct(
								new ClassStruct(List.class),
								new ClassStruct(Integer.class),
								"V"
						)
				)),
				clazz
		);

	}
}
