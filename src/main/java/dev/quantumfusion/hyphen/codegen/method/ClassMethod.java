package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.codegen.FieldEntry;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.info.ClassInfo;
import dev.quantumfusion.hyphen.thr.exception.HyphenException;
import dev.quantumfusion.hyphen.util.GenUtil;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.DUP2;
import static org.objectweb.asm.Opcodes.LADD;

public class ClassMethod extends MethodMetadata<ClassInfo> {
	private final Map<FieldEntry, SerializerDef> fields;

	private ClassMethod(ClassInfo info, Map<FieldEntry, SerializerDef> fields) {
		super(info);
		this.fields = fields;
	}

	public static ClassMethod create(ClassInfo info, ScanHandler handler) {
		var methodMetadata = new ClassMethod(info, new LinkedHashMap<>());
		handler.methods.put(info, methodMetadata);

		if (handler.implementations.containsKey(info.getClazz())) {
			methodMetadata.addField(null, handler.implementations.get(info.getClazz()).apply(info));
			return methodMetadata;
		}

		//check if it exists / if its accessible
		info.findConstructor(handler);
		for (FieldEntry fieldInfo : info.getAllFields(handler)) {
			try {
				methodMetadata.addField(fieldInfo, handler.getDefinition(fieldInfo, info));
			} catch (HyphenException hyphenException) {
				throw hyphenException.addParent(info, fieldInfo.name());
			}
		}

		return methodMetadata;
	}

	public void addField(FieldEntry entry, SerializerDef def) {
		fields.put(entry, def);
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public boolean dynamicSize() {
		return true;
	}

	@Override
	public void writePut(MethodHandler mh, MethodHandler.Var io, MethodHandler.Var data) {
		GenUtil.load(io, data);
		int i = 0;

		for (var entry : this.fields.entrySet()) {
			if (++i < this.fields.size()) {
				mh.visitInsn(DUP2);
				// (io | data |) io | data
			}

			var field = entry.getKey();
			if (field != null) GenUtil.getFieldFromClass(mh, this.info, field);

			// io | field
			entry.getValue().doPut(mh);
		}

		mh.returnOp();
	}

	@Override
	public void writeMeasure(MethodHandler mh, MethodHandler.Var data) {
		boolean first = true;
		for (var entry : this.fields.entrySet()) {

			var field = entry.getKey();
			final SerializerDef value = entry.getValue();
			if (field != null && value.needsField()) {
				data.load();
				GenUtil.getFieldFromClass(mh, this.info, field);
			}
			value.doMeasure(mh);
			if (!first) mh.visitInsn(LADD);
			else first = false;
		}
		mh.returnOp();
	}

	@Override
	public void writeGet(MethodHandler mh, MethodHandler.Var io) {
		if (this.fields.containsKey(null)) {
			io.load();
			this.fields.get(null).doGet(mh);
		} else {
			GenUtil.newDup(mh, this.info);
			for (var def : this.fields.values()) {
				io.load();
				def.doGet(mh);
			}

			// OBJECT | OBJECT | ... fields
			mh.callSpecialMethod(this.info.getClazz(), "<init>", null, this.info.constructorParameters);
		}
		mh.returnOp();
	}

	@Override
	public StringBuilder toFancyString(StringBuilder sb) {
		sb.append(this.getInfo().toFancyString()).append("\n");
		this.fields.forEach(((fieldEntry, serializerDef) -> {
			sb.append(" >-> ").append(fieldEntry.name()).append(": ");
			serializerDef.toFancyString(sb).append("\n");
		}));
		return sb.append('\n');
	}
}
