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
	public static final int MODE = 1;

	public final Map<FieldEntry, ObjectSerializationDef> fields;

	public ClassSerializerMetadata(ClassInfo clazz) {
		super(clazz);
		this.fields = new LinkedHashMap<>();
	}

	@Override
	public void writeEncode(MethodVisitor mv, TypeInfo parent, Context context) {
		this.fields.forEach((fieldEntry, objectSerializationDef) -> {
			if(fieldEntry != null) {
				if(MODE == 0){
					objectSerializationDef.writeEncode(mv, parent, fieldEntry, context, () -> {
						context.data().run();
						mv.visitFieldInsn(GETFIELD, Type.getInternalName(parent.getClazz()), fieldEntry.name(), Type.getDescriptor(fieldEntry.clazz().getClazz()));
					});
				} else {
					this.loadField(mv, parent, context, fieldEntry);
					objectSerializationDef.writeEncode2(mv, context);
				}
			}
		});
	}

	private void loadField(MethodVisitor mv, TypeInfo parent, Context ctx, FieldEntry fieldEntry) {
		ctx.var().IntInsnVar("io", ALOAD);
		ctx.var().IntInsnVar("data", ALOAD);
		Class<?> clazz = fieldEntry.clazz().getClazz();
		mv.visitFieldInsn(GETFIELD, Type.getInternalName(parent.getClazz()), fieldEntry.name(), Type.getDescriptor(clazz));

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

		fields.forEach((fieldEntry, objectSerializationDef) -> {
			if(MODE == 0) {
				objectSerializationDef.writeDecode(mv, parent, fieldEntry, ctx);
			} else {
				ctx.var().IntInsnVar("io", ALOAD);
				objectSerializationDef.writeDecode2(mv, ctx);
			}
		});

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
