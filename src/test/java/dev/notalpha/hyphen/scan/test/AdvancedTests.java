package dev.notalpha.hyphen.scan.test;

import dev.notalpha.hyphen.scan.StructScanner;
import dev.notalpha.hyphen.scan.struct.ClassStruct;
import dev.notalpha.hyphen.scan.struct.ParameterStruct;
import dev.notalpha.hyphen.scan.struct.Struct;
import dev.notalpha.hyphen.scan.struct.TypeStruct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AdvancedTests {

	public static class Value<O, V extends List<O>> {

	}

	@Test
	void basic() {
		StructScanner clazzifier = new StructScanner();
		Struct clazz = clazzifier.scan(Value.class, null);
		System.out.println(clazz.simpleString());
		ParameterStruct oParameter = new ParameterStruct(
				ClassStruct.OBJECT,
				"O"
		);
		ClassStruct expected = new ClassStruct(List.of(), Value.class, List.of(
				oParameter,
				new ParameterStruct(
						new ClassStruct(List.of(), List.class, List.of(
								new ParameterStruct(
										new TypeStruct(oParameter),
										"E"
								)
						)),
						ClassStruct.OBJECT,
						"V"
				)
		));
		Assertions.assertEquals(
				expected,
				clazz
		);
	}
}
