package dev.quantumfusion.hyphen.scan.test;

import dev.quantumfusion.hyphen.scan.StructField;
import dev.quantumfusion.hyphen.scan.StructScanner;
import dev.quantumfusion.hyphen.scan.TestUtils;
import dev.quantumfusion.hyphen.scan.data.Dog;
import dev.quantumfusion.hyphen.scan.struct.*;
import dev.quantumfusion.hyphen.scan.struct.Struct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InheritanceTests {
	public static class Meat extends Food {
		int protein;
	}

	public static class Food {
		int calories;
	}


	@Test
	void basic() throws NoSuchFieldException {
		StructScanner clazzifier = new StructScanner();
		ClassStruct struct = (ClassStruct) clazzifier.scan(Meat.class, null);

		List<StructField> fields = struct.getFields(clazzifier);
		Assertions.assertEquals(
				List.of(
						new StructField(
								Meat.class.getDeclaredField("protein"),
								new ClassStruct(int.class)
						)
				),
				fields
		);

		Struct aSuper = struct.getSuper(clazzifier);
		Assertions.assertEquals(
				new ClassStruct(Food.class),
				aSuper
		);
	}

	ExtraValue<Dog> value;

	@Test
	void parameterized() throws NoSuchFieldException {
		StructScanner clazzifier = new StructScanner();
		ClassStruct struct = (ClassStruct) clazzifier.scan(TestUtils.getField("value").getAnnotatedType(), null);

		Struct aSuper = struct.getSuper(clazzifier);
		ParameterStruct valueO = new ParameterStruct(
				new ClassStruct(Object.class),
				// O sources from parameter V
				new TypeStruct(
						new ParameterStruct(
								new ClassStruct(Object.class),
								new ClassStruct(List.of(TestUtils.PET), Dog.class),
								"V")
				),
				"O");
		Assertions.assertEquals(
				new ClassStruct(
						List.of(), Value.class,
						List.of(
								valueO
						)
				),
				aSuper
		);

		ClassStruct superStruct = (ClassStruct) aSuper;

		List<StructField> fields = superStruct.getFields(clazzifier);
		Assertions.assertEquals(
				List.of(
						new StructField(
								Value.class.getDeclaredField("value"),
								new TypeStruct(valueO)
						)
				),
				fields
		);

		Assertions.assertEquals(Object.class, fields.get(0).type.getBytecodeClass());
		Assertions.assertEquals(Dog.class, fields.get(0).type.getValueClass());
	}

	public static class ExtraValue<V> extends Value<V> {
		List<V> extra;
	}

	public static class Value<O> {
		O value;
	}

	Value<Dog> backwardsValue;


	@Test
	void backwardsValue() throws NoSuchFieldException {
		StructScanner clazzifier = new StructScanner();
		ClassStruct value = (ClassStruct) clazzifier.scan(TestUtils.getField("backwardsValue").getAnnotatedType(), null);
		ClassStruct extraValue = (ClassStruct) clazzifier.scan(ExtraValue.class, null);

		Assertions.assertEquals(
				new ClassStruct(
						List.of(), Value.class,
						List.of(
								new ParameterStruct(
										new ClassStruct(Object.class),
										new ClassStruct(List.of(TestUtils.PET), Dog.class),
										"O")
						)
				),
				value
		);


		ClassStruct extraValueSuper = extraValue.getSuper(clazzifier);
		extraValueSuper.resolve(value);

		ParameterStruct vResolved = new ParameterStruct(
				new ClassStruct(Object.class),
				new ParameterStruct(
						List.of(), ClassStruct.OBJECT,
						new ClassStruct(List.of(TestUtils.PET), Dog.class),
						"O"
				),
				"V");
		Assertions.assertEquals(
				new ClassStruct(
						List.of(), ExtraValue.class,
						List.of(
								vResolved
						)
				),
				extraValue
		);

		List<StructField> fields = extraValue.getFields(clazzifier);
		Assertions.assertEquals(
				List.of(
						new StructField(
								ExtraValue.class.getDeclaredField("extra"),
								new ClassStruct(
										List.of(), List.class,
										List.of(
												new ParameterStruct(
														new TypeStruct(vResolved),
														"E"
												)
										)
								)
						)
				),
				fields
		);
	}

}
