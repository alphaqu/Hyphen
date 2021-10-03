package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.Vars;
import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.info.SubclassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.HyphenException;
import dev.quantumfusion.hyphen.util.GenUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;

public class SubclassMethod extends MethodMetadata<SubclassInfo> {
	private final Map<Class<?>, SerializerDef> subtypes = new LinkedHashMap<>();

	public SubclassMethod(SubclassInfo info) {
		super(info);
	}

	public static SubclassMethod create(SubclassInfo info, ScanHandler handler) {
		var methodMetadata = new SubclassMethod(info);
		handler.methods.put(info, methodMetadata);
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

	@Override
	public long getSize() {
		return 1;
	}

	@Override
	public boolean dynamicSize() {
		return true;
	}

	@Override
	public void writePut(MethodHandler mh) {
		var io = Vars.IO.get(mh);
		var data = Vars.DATA.get(mh);
		AtomicInteger i = new AtomicInteger(0);
		if (this.info.supportsNull()) GenUtil.nullCheckReturn(mh, data, () -> {
			io.load();
			mh.visitInsn(ICONST_M1);
			mh.callIOPut(byte.class);
		});

		// store data
		data.load();
		mh.callInstanceMethod(Object.class, "getClass", Class.class);
		var clazz = mh.createVar("clazz", Class.class);
		clazz.store();
		for (var entry : this.subtypes.entrySet()) {
			Class<?> clz = entry.getKey();
			GenUtil.ifElseClass(mh, clz, clazz, () -> {
				GenUtil.load(io, data, io);
				mh.visitLdcInsn(i.getAndIncrement());
				mh.callIOPut(byte.class);
				mh.cast(clz);
				entry.getValue().writePut(mh);
				mh.returnOp();
			});
		}

		// TODO: throw error
		mh.returnOp();
	}

	@Override
	public void writeGet(MethodHandler mh) {
		var io = Vars.IO.get(mh);
		io.load();
		// io
		mh.visitInsn(DUP);
		// io | io
		mh.callIOGet(byte.class);
		// io | int
		int nullable = this.info.supportsNull() ? 1 : 0;
		int count = this.subtypes.size() + nullable;
		try (var switchVar = mh.createSwitch(-nullable, count - 1 - nullable, count)) {
			if (nullable > 0)
				switchVar.nextLabel(() -> mh.visitInsn(ACONST_NULL));

			for (var entry : this.subtypes.values())
				switchVar.nextLabel(() -> entry.writeGet(mh));

			switchVar.defaultLabel(() -> {
				mh.visitInsn(POP);
				mh.visitInsn(ACONST_NULL);
			});
		}
	}

	@Override
	public void writeMeasure(MethodHandler mh) {
		var data = Vars.DATA.get(mh);
		if (this.info.supportsNull()) GenUtil.nullCheckReturn(mh, data, () -> mh.visitInsn(LCONST_1));

		data.load();
		mh.callInstanceMethod(Object.class, "getClass", Class.class);
		var clazz = mh.createVar("clazz", Class.class);
		clazz.store();

		for (Map.Entry<Class<?>, SerializerDef> entry : this.subtypes.entrySet()) {
			Class<?> clz = entry.getKey();
			GenUtil.ifElseClass(mh, clz, clazz, () -> {
				data.loadCast(clz);
				entry.getValue().writeMeasure(mh);
				GenUtil.addL(mh, 1L);
				mh.returnOp();
			});
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
	}

	@Override
	public StringBuilder toFancyString(StringBuilder sb) {
		sb.append(this.getInfo().toFancyString()).append("\n");
		this.subtypes.forEach(((cls, serializerDef) -> {
			sb.append(" >-> ").append(cls.getSimpleName()).append(": ");
			serializerDef.toFancyString(sb).append("\n");
		}));
		return sb.append('\n');
	}
}
