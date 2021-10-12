package dev.quantumfusion.hyphen.sped;

import dev.quantumfusion.hyphen.Direction;
import dev.quantumfusion.hyphen.FieldEntry;
import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.annotations.Data;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.scan.poly.classes.CoWrappedC1;
import dev.quantumfusion.hyphen.type.Clazz;

import java.util.List;

public class TestThings {
	@Data
	public CoWrappedC1<String, C2<String>> things = new CoWrappedC1<String, C2<String>>(new C2<>("420", "69"), "fdsas");

	public static void main(String[] args) throws NoSuchFieldException {
		final Clazz things = ScanHandler.create(TestThings.class.getField("things").getAnnotatedType(), null, Direction.NORMAL);
		scan(things);
	}

	public static void scan(Clazz clazz) {
		System.out.println(clazz);
		for (FieldEntry field : clazz.getFields()) {
			System.out.println("\t" + field);
		}
		System.out.println();

		for (FieldEntry field : clazz.getFields()) {
			scan(ScanHandler.create(field.field().getAnnotatedType(), clazz, Direction.NORMAL));
		}
	}
}
