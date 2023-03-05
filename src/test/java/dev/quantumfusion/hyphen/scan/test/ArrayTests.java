package dev.quantumfusion.hyphen.scan.test;

import dev.quantumfusion.hyphen.scan.StructField;
import dev.quantumfusion.hyphen.scan.StructScanner;
import dev.quantumfusion.hyphen.scan.TestUtils;
import dev.quantumfusion.hyphen.scan.data.Apple;
import dev.quantumfusion.hyphen.scan.data.Banana;
import dev.quantumfusion.hyphen.scan.data.Tax;
import dev.quantumfusion.hyphen.scan.struct.*;
import dev.quantumfusion.hyphen.scan.struct.Struct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ArrayTests {

	int[] basic;

	@Test
	void basic() throws NoSuchFieldException {
		StructScanner structScanner = new StructScanner();
		Struct basic = structScanner.scan(getClass().getDeclaredField("basic").getAnnotatedType(), null);

		Assertions.assertEquals(
				new ArrayStruct(
						new ClassStruct(int.class)
				)
				, basic);

		Assertions.assertEquals(int[].class, basic.getBytecodeClass());
		Assertions.assertEquals(int[].class, basic.getValueClass());
	}

	@Banana Object @Apple [] basicObject;

	@Test
	void basicObject() throws NoSuchFieldException {
		StructScanner structScanner = new StructScanner();
		Struct basic = structScanner.scan(getClass().getDeclaredField("basicObject").getAnnotatedType(), null);

		Assertions.assertEquals(
				new ArrayStruct(
						List.of(TestUtils.APPLE),
						new ClassStruct(List.of(TestUtils.BANANA), Object.class)
				)
				, basic);

		Assertions.assertEquals(Object[].class, basic.getBytecodeClass());
		Assertions.assertEquals(Object[].class, basic.getValueClass());
	}

	public static class C0<O> {
		O @Apple [] array;
	}

	C0<? extends Tax> thing;

	@Test
	void typeVariable() throws NoSuchFieldException {
		StructScanner structScanner = new StructScanner();


		ClassStruct basic = (ClassStruct) structScanner.scan(getClass().getDeclaredField("thing").getAnnotatedType(), null);


		List<StructField> fields = basic.getFields(structScanner);
		Assertions.assertEquals(
				List.of(
						new StructField(
								C0.class.getDeclaredField("array"),
								new ArrayStruct(
										List.of(TestUtils.APPLE),
										new TypeStruct(new ParameterStruct(new WildcardStruct(new ClassStruct(Tax.class), false), "O"))
								)
						)
				),
				fields
		);

		Assertions.assertEquals(Object[].class, fields.get(0).type.getBytecodeClass());
		Assertions.assertEquals(Tax[].class, fields.get(0).type.getValueClass());

		var value = new C0<Tax>();
		value.array = new Tax[2];
		Tax[] array = value.array;
	}
}
