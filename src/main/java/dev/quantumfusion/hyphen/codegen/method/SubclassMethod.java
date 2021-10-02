package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.info.SubclassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.HyphenException;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import java.util.Iterator;
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
			if (subtypes.containsKey(subTypeInfo.getClazz())) {
				// TODO: throw error, cause there is a duplicated class
				//		 or should this be done earlier
			}

			subtypes.put(subTypeInfo.getClazz(), handler.getDefinition(null, subTypeInfo, null));
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
			mh.callIOPut(byte.class);
			// io | data
			mh.cast(clz);
			// io | data as clz
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
		mh.callIOGet(byte.class);
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

	@Override
	public long getSize() {
		Iterator<SerializerDef> iterator = this.subtypes.values().iterator();

		if (!iterator.hasNext()) throw new IllegalStateException("no subclasses");

		var size = iterator.next().getSize();

		if (size < 0)
			// dynamic sized subclass
			return ~1;

		while (iterator.hasNext()) {
			var otherSize = iterator.next().getSize();
			if (otherSize != size)
				// differently sized subsizes
				return ~1;
		}

		return 1 + size;
	}

	@Override
	public void writeMeasure(MethodHandler mh, MethodHandler.Var data) {
		data.load();
		// data
		mh.callInstanceMethod(Object.class, "getClass", Class.class);
		// clazz

		for (Map.Entry<Class<?>, SerializerDef> entry : this.subtypes.entrySet()) {
			Class<?> clz = entry.getKey();
			SerializerDef def = entry.getValue();
			Label skip = new Label();

			mh.visitInsn(DUP);
			// clazz | clazz
			mh.visitLdcInsn(Type.getType(clz));
			// clazz | clazz | clz
			mh.visitJumpInsn(IF_ACMPNE, skip);
			// clazz == clz
			mh.visitInsn(POP);
			// --

			var size = def.getSize();
			if (size >= 0) {
				mh.visitLdcInsn(size);
			} else {
				data.load();
				// data
				mh.cast(clz);
				// data as clz
				def.calcSubSize(mh);
				// size
				if (size < -1) {
					mh.visitLdcInsn(~size);
					mh.visitInsn(LADD);
				}
			}
			mh.returnOp();
			mh.visitLabel(skip);
		}

		ThrowHandler.fatalGen(mh, HyphenException.class, "Subclass Unsupported",
				() -> {
					data.load();
					mh.callInstanceMethod(Object.class, "getClass", Class.class);
					return "Clazz";
				},
				() -> {
					mh.visitLdcInsn("dumb");
					return "Feelings";
				});
		mh.visitInsn(LCONST_0);
		mh.returnOp();
	}
}
