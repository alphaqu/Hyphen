package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.codegen.FieldEntry;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.info.ClassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.thr.exception.HyphenException;
import dev.quantumfusion.hyphen.util.ScanUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class ClassMethod extends MethodMetadata {
	private final Map<FieldEntry, SerializerDef> fields;

	private ClassMethod(TypeInfo info, Map<FieldEntry, SerializerDef> fields) {
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
		ScanUtils.checkConstructor(handler, info);
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
		io.load();
		data.load();
		// io data
		int i = 0;

		for (var entry : this.fields.entrySet()) {
			if (++i < this.fields.size()) {
				mh.visitInsn(DUP2);
				// (io | data |) io | data
			}

			var field = entry.getKey();
			if (field != null) {
				TypeInfo fieldType = field.clazz();
				mh.getField(GETFIELD, this.info.getClazz(), field.name(), fieldType.getRawType());
				if (!fieldType.getClazz().isAssignableFrom(fieldType.getRawType())) {
					mh.cast(fieldType.getClazz());
				}
			}
			// io | field
			entry.getValue().doPut(mh);
		}

		mh.returnOp();
	}

	@Override
	public void writeGet(MethodHandler mh, MethodHandler.Var io) {
		if (this.fields.containsKey(null)) {
			SerializerDef serializerDef = this.fields.get(null);
			io.load();
			serializerDef.doGet(mh);
		} else {
			mh.typeInsn(NEW, this.info.getClazz());
			mh.visitInsn(DUP);
			// OBJECT | OBJECT

			for (var def : this.fields.values()) {
				io.load();
				def.doGet(mh);
			}

			// OBJECT | OBJECT | ... fields
			mh.callSpecialMethod(this.info.getClazz(),
					"<init>",
					null,
					this.fields.keySet()
							.stream()
							.map(FieldEntry::clazz)
							.map(TypeInfo::getRawType)
							.toArray(Class[]::new));
		}
		mh.returnOp();
	}

	@Override
	public void writeMeasure(MethodHandler mh, MethodHandler.Var data) {
		boolean first = true;
		for (var entry : this.fields.entrySet()) {
			data.load();
			// (size |) data

			var field = entry.getKey();
			final SerializerDef value = entry.getValue();
			if (field != null && value.needsField()) {
				TypeInfo fieldType = field.clazz();
				mh.getField(GETFIELD, this.info.getClazz(), field.name(), fieldType.getRawType());
				if (!fieldType.getClazz().isAssignableFrom(fieldType.getRawType())) {
					mh.cast(fieldType.getClazz());
				}
			}
			value.doMeasure(mh);
			if (!first) mh.visitInsn(LADD);
			else first = false;
		}
		mh.returnOp();
	}
}
