package dev.quantumfusion.hyphen.sped;

import dev.quantumfusion.hyphen.Direction;
import dev.quantumfusion.hyphen.FieldEntry;
import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.annotations.Data;
import dev.quantumfusion.hyphen.scan.poly.classes.C0;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.scan.poly.classes.CoWrappedC1;
import dev.quantumfusion.hyphen.type.Clazz;

import java.util.List;

public class TestThings {
	@Data
	public CoWrappedC1<String, C2<String>> things;
	@Data
	public C2<C1<C0>> thigns2;

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

		List<FieldEntry> fields = clazz.getFields();
		for (FieldEntry field : fields) {
			scan(field.clazz());
		}
	}
}
