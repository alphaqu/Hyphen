package dev.quantumfusion.hyphen.gen.metadata;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.gen.VarHandler;
import dev.quantumfusion.hyphen.gen.impl.ObjectSerializationDef;
import dev.quantumfusion.hyphen.info.SubclassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.Color;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class SubclassSerializerMetadata extends SerializerMetadata {
	private final Map<Class<?>, ? extends ObjectSerializationDef> subtypes;

	public SubclassSerializerMetadata(SubclassInfo clazz, Map<Class<?>, ? extends ObjectSerializationDef> subtypes) {
		super(clazz);
		this.subtypes = subtypes;
	}

	private static final int WE_VERSION = 1;


	public void we1(MethodVisitor mv, TypeInfo parent, Context context) {
		VarHandler var = context.var();

		// TODO: allow more than 256 subclasses
		assert this.subtypes.size() <= 256;
		byte i = 0;

		var io = var.getVar("io");
		var data = var.getVar("data");

		io.visitIntInsn(mv, ALOAD);
		data.visitIntInsn(mv, ALOAD);
		// io data

		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
		// io dataClazz

		Label end = new Label();

		for (var entry : this.subtypes.entrySet()) {
			Class<?> key = entry.getKey();
			ObjectSerializationDef serializer = entry.getValue();

			// io dataClazz
			Label l = new Label();
			if (i + 1 < this.subtypes.size()) {
				mv.visitInsn(DUP);
				// io dataClazz dataClazz
				mv.visitLdcInsn(Type.getType(key));
				// io dataClazz dataClazz clazz
				mv.visitJumpInsn(IF_ACMPNE, l);
				// io dataClazz(==clazz)
				mv.visitInsn(POP);
			} else {
				// last one
				// io dataClazz
				mv.visitLdcInsn(Type.getType(key));
				// io dataClazz clazz
				mv.visitJumpInsn(IF_ACMPNE, l);
			}
			// io
			mv.visitInsn(DUP);
			// io io
			mv.visitLdcInsn(i);
			// io io i
			context.mode().callMethod(mv, "putByte", GenUtil.getVoidMethodDesc(byte.class));
			// io
			data.visitIntInsn(mv, ALOAD);
			// io data
			mv.visitTypeInsn(CHECKCAST, Type.getInternalName(key));
			// io data
			serializer.writeEncode2(mv, context);
			// --
			mv.visitJumpInsn(GOTO, end);
			mv.visitLabel(l);
			i++;
		}

		mv.visitInsn(POP);

		// TODO: throw?
		mv.visitLabel(end);
	}

	@Override
	public void writeEncode(MethodVisitor mv, TypeInfo parent, Context context) {
		if (WE_VERSION == 1) {
			this.we1(mv, parent, context);
		}
	}

	@Override
	public void writeDecode(MethodVisitor mv, TypeInfo parent, Context ctx) {
		VarHandler var = ctx.var();
		var io = var.getVar("io");

		io.visitIntInsn(mv, ALOAD);
		mv.visitInsn(DUP);
		// io io
		ctx.mode().callMethod(mv, "getByte", GenUtil.getMethodDesc(byte.class));

		Label[] labels = new Label[this.subtypes.size()];

		for (int i = 0; i < this.subtypes.size(); i++) {
			labels[i] = new Label();
		}

		Label def = new Label();
		mv.visitTableSwitchInsn(0, this.subtypes.size() - 1, def, labels);

		int i = 0;
		for (ObjectSerializationDef value : this.subtypes.values()) {
			mv.visitLabel(labels[i++]);
			// io
			value.writeDecode2(mv, ctx);
			// obj
			mv.visitInsn(ARETURN);
		}

		mv.visitLabel(def);
		// io
		mv.visitInsn(POP);
		mv.visitInsn(ACONST_NULL);
	}

	public String toFancyString() {
		StringBuilder sb = new StringBuilder(super.toFancyString());
		sb.append('\n');
		this.subtypes.forEach((clazz, info) -> {
			sb.append(Color.YELLOW).append('\t').append(clazz.getSimpleName());
			sb.append(Color.WHITE).append(" ==> ");
			sb.append(Color.BLUE).append(info.toFancyString());
			sb.append('\n');
		});
		return sb.toString();
	}
}
