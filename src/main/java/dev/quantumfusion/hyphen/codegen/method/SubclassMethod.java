package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.info.SubclassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class SubclassMethod extends MethodMetadata {
	private final Map<Class<?>, SerializerDef> subtypes = new LinkedHashMap<>();

	public SubclassMethod(SubclassInfo info) {
		super(info);
	}

	public static SubclassMethod create(SubclassInfo info, ScanHandler handler) {
		var methodMetadata = new SubclassMethod(info);
		final Map<Class<?>, SerializerDef> subtypes = methodMetadata.subtypes;
		for (TypeInfo subTypeInfo : info.classInfos) {
			if (subtypes.containsKey(subTypeInfo.clazz)) {
				// TODO: throw error, cause there is a duplicated class
				//		 or should this be done earlier
			}

			subtypes.put(subTypeInfo.clazz, handler.getDefinition(null, subTypeInfo, null));
		}
		return methodMetadata;
	}

	public void addSubclass(Class<?> clazz, SerializerDef def) {
		subtypes.put(clazz, def);
	}

	@Override
	public void writePut(MethodHandler mh, MethodHandler.Var io, MethodHandler.Var data) {
		int i = 0;

		io.load();
		// io
		data.load();
		// io | data
		mh.visitInsn(DUP2);
		// io | data | io | data
		mh.callInstanceMethod(Object.class, "getClass", Class.class);
		// io | data | io | clazz

		for (Map.Entry<Class<?>, SerializerDef> entry : this.subtypes.entrySet()) {
			Class<?> clz = entry.getKey();
			SerializerDef def = entry.getValue();
			Label skip = new Label();

			mh.visitInsn(DUP);
			// io | data | io | clazz | clazz
			mh.visitLdcInsn(Type.getType(clz));
			// io | data | io | clazz | clazz | clz
			mh.visitJumpInsn(IF_ACMPNE, skip);
			// io | data | io | clazz == clz
			mh.visitInsn(POP);
			// io | data | io
			mh.visitLdcInsn(i++);
			// io | data | io | i
			mh.callIOPut(int.class);
			// io | data
			def.doPut(mh);
			// --
			mh.returnOp();
			mh.visitLabel(skip);
		}

		// io | data | io | clazz
		// TODO: throw error
		mh.visitInsn(POP2);
		mh.visitInsn(POP2);
		mh.returnOp();
	}

	@Override
	public void writeGet(MethodHandler mh, MethodHandler.Var io) {
		int i = 0;

		io.load();
		// io
		mh.visitInsn(DUP);
		// io | io
		mh.callIOGet(int.class);
		// io | int

		int count = this.subtypes.size();

		var def = new Label();
		var labels = new Label[count];

		for (int x = 0; x < count; x++) {
			labels[x] = new Label();
		}

		mh.visitTableSwitchInsn(0, count - 1, def, labels);
		// io

		for (var entry : this.subtypes.values()) {
			mh.visitLabel(labels[i++]);
			// io
			entry.doGet(mh);
			// data
			mh.returnOp();
		}

		mh.visitLabel(def);
		// io
		// TODO: throw error
		mh.visitInsn(POP);
		mh.visitInsn(ACONST_NULL);
		mh.returnOp();
	}
}
