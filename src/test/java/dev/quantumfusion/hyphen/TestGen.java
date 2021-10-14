package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.scan.Direction;
import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.CoWrappedC1;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.util.TestUtil;

import java.util.List;

public class TestGen {
	CoWrappedC1<List<String>, C1<List<String>>> wrappedC1;
	public static void main(String[] args) throws NoSuchFieldException {
		final Clazz wrappedC1 = Clazzifier.create(TestGen.class.getDeclaredField("wrappedC1").getAnnotatedType(), null, Direction.NORMAL);

		for (FieldEntry field : wrappedC1.getFields()) {
			System.out.println(field);
		}
	}

}
