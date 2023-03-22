package dev.notalpha.hyphen.scan.test;

import dev.notalpha.hyphen.scan.StructField;
import dev.notalpha.hyphen.scan.TestUtils;
import dev.notalpha.hyphen.scan.data.Apple;
import dev.notalpha.hyphen.scan.struct.ClassStruct;
import dev.notalpha.hyphen.scan.struct.ParameterStruct;
import dev.notalpha.hyphen.scan.struct.TypeStruct;
import dev.notalpha.hyphen.scan.struct.WildcardStruct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FormatTests {
	int stuff;
	@Test
	void classStruct() {
		Assertions.assertEquals("int(@" + Apple.class.getName() + "())", new ClassStruct(List.of(TestUtils.APPLE), int.class).toString());
	}

	@Test
	void parameterizedClassStruct() {
		Assertions.assertEquals("int(@" + Apple.class.getName() + "())<K=boolean>",
				new ClassStruct(List.of(TestUtils.APPLE), int.class,
						List.of(
								 new ParameterStruct(ClassStruct.OBJECT, new ClassStruct(boolean.class), "K")
						)

				).toString());
	}

	@Test
	void parameterizedBoundClassStruct() {
		Assertions.assertEquals("int(@" + Apple.class.getName() + "())<K{String}=boolean>",
				new ClassStruct(List.of(TestUtils.APPLE), int.class,
						List.of(
								 new ParameterStruct(new ClassStruct(String.class), new ClassStruct(boolean.class), "K")
						)
				).toString());
	}

	@Test
	void typeStruct() {
		Assertions.assertEquals("{null}",
				new TypeStruct( List.of(), null).toString()
		);
	}

	@Test
	void annotatedTypeStruct() {
		Assertions.assertEquals("{O}(@" + Apple.class.getName() + "())",
				new TypeStruct(List.of(TestUtils.APPLE), new ParameterStruct(ClassStruct.OBJECT, ClassStruct.OBJECT, "O")).toString()
		);
	}

	@Test
	void wildcardStruct() {
		Assertions.assertEquals("?(@" + Apple.class.getName() + "())",
				new WildcardStruct(List.of(TestUtils.APPLE)).toString()
		);
	}

	@Test
	void wildcardUpperBoundStruct() {
		Assertions.assertEquals("?(@" + Apple.class.getName() + "()) extends boolean",
				new WildcardStruct(List.of(TestUtils.APPLE), new ClassStruct(boolean.class), false).toString()
		);
	}
	@Test
	void wildcardLowerBoundStruct() {
		Assertions.assertEquals("?(@" + Apple.class.getName() + "()) super String",
				new WildcardStruct(List.of(TestUtils.APPLE), new ClassStruct(String.class), true).toString()
		);
	}

	@Test
	void field() throws NoSuchFieldException {
		Assertions.assertEquals("stuff: int",
				new StructField(FormatTests.class.getDeclaredField("stuff"), new ClassStruct(int.class)).toString()
		);
	}

}
