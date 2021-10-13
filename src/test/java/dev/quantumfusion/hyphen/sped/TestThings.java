package dev.quantumfusion.hyphen.sped;

import dev.quantumfusion.hyphen.scan.Direction;
import dev.quantumfusion.hyphen.scan.FieldEntry;
import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.poly.classes.C0;
import dev.quantumfusion.hyphen.scan.poly.classes.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.C2;
import dev.quantumfusion.hyphen.scan.poly.classes.CoWrappedC1;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestThings {
	@Data
	public CoWrappedC1<String, C2<String>> things;
	@Data
	public C2<C1<C0>> thigns2;
	@Data
	public C1<C2<Object>> thing3;

	@Test
	public void main() throws NoSuchFieldException {
		final Clazz things = Clazzifier.create(TestThings.class.getField("thing3").getAnnotatedType(), null, Direction.NORMAL);
		scan(things, C2.class);
	}

	public static void scan(Clazz clazz, Class<?> cls) {
		System.out.println(clazz);
		for (FieldEntry field : cls == null ? clazz.getFields() : clazz.asSub(cls)) {
			System.out.println("\t" + field);
		}
		System.out.println();

		List<FieldEntry> fields = clazz.getFields();
		for (FieldEntry field : fields) {
			scan(field.clazz(), null);
		}
	}
}
