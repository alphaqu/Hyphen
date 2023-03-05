package dev.quantumfusion.hyphen.scan.test;

import dev.quantumfusion.hyphen.scan.StructField;
import dev.quantumfusion.hyphen.scan.StructScanner;
import dev.quantumfusion.hyphen.scan.TestUtils;
import dev.quantumfusion.hyphen.scan.data.Apple;
import dev.quantumfusion.hyphen.scan.data.Banana;
import dev.quantumfusion.hyphen.scan.struct.ClassStruct;
import dev.quantumfusion.hyphen.scan.struct.Struct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@Apple
public class SimpleClazzTests {

	int type;

	@Test
	void basic() throws NoSuchFieldException {
		StructScanner clazzifier = new StructScanner();
		Struct clazz = clazzifier.scan(this.getClass().getDeclaredField("type").getAnnotatedType(), null);
		Assertions.assertEquals(
				new ClassStruct(int.class),
				clazz
		);

		Assertions.assertEquals(clazz.getBytecodeClass(), int.class);
		Assertions.assertEquals(clazz.getValueClass(), int.class);
	}

	@Apple int annotated;

	@Test
	void annotated() throws NoSuchFieldException {
		StructScanner clazzifier = new StructScanner();
		Assertions.assertEquals(
				new ClassStruct(List.of(TestUtils.APPLE), int.class),
				clazzifier.scan(this.getClass().getDeclaredField("annotated").getAnnotatedType(), null)
		);
	}

	@Apple
	int annotatedField;

	@Test
	void annotatedField() throws NoSuchFieldException {
		StructScanner clazzifier = new StructScanner();
		Assertions.assertEquals(
				new ClassStruct(List.of(TestUtils.APPLE), int.class),
				clazzifier.scan(this.getClass().getDeclaredField("annotatedField").getAnnotatedType(), null)
		);
	}


	@Banana
	public static class AnnotatedObject {
		@Apple int value;
	}

	@Apple AnnotatedObject annotatedClazz;

	@Test
	void annotatedClazz() throws NoSuchFieldException {
		StructScanner clazzifier = new StructScanner();
		Struct clazz = clazzifier.scan(this.getClass().getDeclaredField("annotatedClazz").getAnnotatedType(), null);
		Assertions.assertEquals(
				new ClassStruct(List.of(TestUtils.BANANA, TestUtils.APPLE), AnnotatedObject.class),
				clazz
		);

		ClassStruct simpleClazz = (ClassStruct) clazz;
		Assertions.assertEquals(
				List.of(
						new StructField(
								AnnotatedObject.class.getDeclaredField("value"),
							new ClassStruct(List.of(TestUtils.APPLE), int.class)
						)
				),
				simpleClazz.getFields(clazzifier)
		);

		Assertions.assertEquals(clazz.getBytecodeClass(), AnnotatedObject.class);
		Assertions.assertEquals(clazz.getValueClass(), AnnotatedObject.class);
	}
}
