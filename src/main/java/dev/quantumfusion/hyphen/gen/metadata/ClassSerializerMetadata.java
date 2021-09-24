package dev.quantumfusion.hyphen.gen.metadata;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.gen.FieldEntry;
import dev.quantumfusion.hyphen.gen.impl.ObjectSerializationDef;
import dev.quantumfusion.hyphen.info.ClassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.Color;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

public class ClassSerializerMetadata extends SerializerMetadata {
	public final Map<FieldEntry, ObjectSerializationDef> fields;

	public ClassSerializerMetadata(ClassInfo clazz) {
		super(clazz);
		this.fields = new LinkedHashMap<>();
	}

	@Override
	public void writeEncode(MethodVisitor mv, TypeInfo parent, Context context) {
		fields.forEach((fieldEntry, objectSerializationDef) -> {
			objectSerializationDef.writeEncode(mv, parent, fieldEntry, context, () -> {
				context.data().run();
				mv.visitFieldInsn(GETFIELD, Type.getInternalName(parent.getClazz()), fieldEntry.name(), Type.getDescriptor(fieldEntry.clazz().getClazz()));
			});
		});
	}

	@Override
	public void writeDecode(MethodVisitor mv, TypeInfo parent, Context ctx) {
		Class<?> clazz = parent.getClazz();
		String internalName = Type.getInternalName(clazz);

		mv.visitTypeInsn(NEW, internalName);
		mv.visitInsn(DUP);
		Set<FieldEntry> fieldEntries = fields.keySet();
		Type[] types = new Type[fieldEntries.size()];
		int pos = 0;
		for (FieldEntry fieldEntry : fieldEntries) {
			types[pos++] = Type.getType(fieldEntry.clazz().getClazz());
		}

		fields.forEach((fieldEntry, objectSerializationDef) -> objectSerializationDef.writeDecode(mv, parent, fieldEntry, ctx));

		String methodDescriptor = Type.getMethodDescriptor(Type.getType(Void.TYPE), types);
		mv.visitMethodInsn(INVOKESPECIAL, internalName, "<init>", methodDescriptor, false);
	}

	public String toFancyString() {
		StringBuilder sb = new StringBuilder(super.toFancyString());
		sb.append('\n');
		fields.forEach((fieldEntry, objectSerializationDef) -> {
			sb.append(Color.RESET).append('\t').append(fieldEntry == null ? "null" : fieldEntry.name());
			sb.append(Color.RED).append(" : ");
			sb.append(Color.BLUE).append(objectSerializationDef.toFancyString());
			sb.append('\n');
		});
		return sb.toString();
	}

}
