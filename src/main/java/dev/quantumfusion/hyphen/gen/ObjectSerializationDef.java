package dev.quantumfusion.hyphen.gen;

import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.Color;
import org.objectweb.asm.MethodVisitor;

public interface ObjectSerializationDef {
	Class<?> getType();


	void writeEncode(MethodVisitor methodVisitor, TypeInfo parent, FieldEntry fieldEntry, Context context);

	void writeDecode(MethodVisitor methodVisitor, TypeInfo parent, FieldEntry fieldEntry, Context ctx);

	default String toFancyString() {
		return Color.RED + this.toString();
	}
}
