package dev.quantumfusion.hyphen.sped;

import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.scan.Direction;
import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.poly.classes.*;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import java.util.List;

public class TestThings {
	@Data
	public CoWrappedC1<String, C2<String>> things;
	@Data
	public C1<Integer> thigns2;
	@Data
	public C1<C2<Object>> thing3;


	static public void main(String[] args) throws NoSuchFieldException {
		final Clazz things = Clazzifier.create(TestThings.class.getField("thigns2").getAnnotatedType(), null, Direction.NORMAL);
		scan(things.asSub(C2.class));
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
