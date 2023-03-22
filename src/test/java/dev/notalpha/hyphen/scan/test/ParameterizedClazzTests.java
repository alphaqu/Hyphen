package dev.notalpha.hyphen.scan.test;

import dev.notalpha.hyphen.scan.StructField;
import dev.notalpha.hyphen.scan.StructScanner;
import dev.notalpha.hyphen.scan.TestUtils;
import dev.notalpha.hyphen.scan.data.*;
import dev.notalpha.hyphen.scan.struct.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ParameterizedClazzTests {
	List<@Apple Dog> basic;

	@Test
	void basic() throws NoSuchFieldException {
		StructScanner clazzifier = new StructScanner();
		Assertions.assertEquals(
				new ClassStruct(List.of(), List.class, List.of( new ParameterStruct(new ClassStruct(List.of(TestUtils.PET, TestUtils.APPLE), Dog.class), "E"))),
				clazzifier.scan(this.getClass().getDeclaredField("basic").getAnnotatedType(), null)
		);
	}

	List<?> wildcard;

	@Test
	void wildcard() throws NoSuchFieldException {
		StructScanner clazzifier = new StructScanner();
		Assertions.assertEquals(
				new ClassStruct(List.of(), List.class, List.of(
						new ParameterStruct(new WildcardStruct(), "E")
				)),
				clazzifier.scan(this.getClass().getDeclaredField("wildcard").getAnnotatedType(), null)
		);
	}

	public static class AnnotatedType<@Banana O extends @Pet Dog> {
		@Apple O value;
	}

	AnnotatedType<@Apple Tax> value;

	@Test
	void annotated() throws NoSuchFieldException {
		StructScanner clazzifier = new StructScanner();
		ClassStruct o = new ClassStruct(
				List.of(), AnnotatedType.class,
				List.of(new ParameterStruct(
						List.of(TestUtils.BANANA), new ClassStruct(List.of(TestUtils.PET), Dog.class),
						new ClassStruct(List.of(TestUtils.APPLE), Tax.class), "O"))
		);
		Struct clazz = clazzifier.scan(getClass().getDeclaredField("value").getAnnotatedType(), null);
		Assertions.assertEquals(
				o,
				clazz
		);

		ClassStruct clazz1 = (ClassStruct) clazz;
		List<StructField> fields = clazz1.getFields(clazzifier);

		Assertions.assertEquals(
				List.of(
						new StructField(
								AnnotatedType.class.getDeclaredField("value"),
								new TypeStruct(List.of(TestUtils.APPLE), clazz1.getParameter("O"))
						)

				),
				fields
		);

		Assertions.assertEquals(Dog.class, fields.get(0).type.getBytecodeClass());
		Assertions.assertEquals(Tax.class, fields.get(0).type.getValueClass());

	}
}