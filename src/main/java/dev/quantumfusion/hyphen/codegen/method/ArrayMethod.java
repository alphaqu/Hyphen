package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.MethodType;
import dev.quantumfusion.hyphen.codegen.Vars;
import dev.quantumfusion.hyphen.info.ArrayInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.util.GenUtil;

import static org.objectweb.asm.Opcodes.*;

public class ArrayMethod extends MethodMetadata<ArrayInfo> {
	private final TypeInfo values;

	private ArrayMethod(ArrayInfo info) {
		super(info);
		this.values = info.values;
	}

	public static ArrayMethod create(ArrayInfo info, ScanHandler scanHandler) {
		final ArrayMethod arrayMethod = new ArrayMethod(info);
		scanHandler.methods.put(info, arrayMethod);
		scanHandler.createSerializeMethod(info.values);
		return arrayMethod;
	}

	@Override
	public long getSize() {
		return 4; // length
	}

	@Override
	public boolean dynamicSize() {
		return true;
	}

	@Override
	public void writePut(MethodHandler mh) {
		var io = Vars.IO.get(mh);
		var data = Vars.DATA.get(mh);
		mh.pushScope();
		GenUtil.load(io, data);
		try (var forLoop = mh.iterateThroughArrayAndSaveSize().start()) {
			GenUtil.load(io, data);
			// io | data
			forLoop.getFromArray();
			// io | data[i]
			GenUtil.castIfNotAssignable(mh, values);
			mh.callHyphenMethod(MethodType.PUT, values);
		}

		mh.popScope();
		mh.returnOp();
	}

	@Override
	public void writeGet(MethodHandler mh) {
		var io = Vars.IO.get(mh);
		mh.pushScope();
		var array = mh.createVar("array", this.info.getClazz());
		io.load();
		// io
		mh.callIOGet(int.class);
		// length
		mh.visitInsn(DUP);
		// length | length
		var aFor = mh.createForWithLength();
		// length
		mh.typeInsn(ANEWARRAY, this.values.getClazz());
		// arr
		array.store();
		try (var forLoop = aFor.start()) {
			GenUtil.load(array, forLoop.i, io);
			// array | i | io
			mh.callHyphenMethod(MethodType.GET, values);
			// array | i | component
			mh.visitInsn(AASTORE);
		}
		array.load();
		mh.returnOp();
		mh.popScope();
	}

	@Override
	public void writeMeasure(MethodHandler mh) {
		var data = Vars.DATA.get(mh);
		mh.pushScope();
		data.load();
		// data
		mh.visitInsn(ARRAYLENGTH);
		// length
		var aFor = mh.createForWithLength();

		mh.visitLdcInsn(4L);
		// size
		try (var forLoop = aFor.start()) {
			data.load();
			// size | data
			forLoop.getFromArray();
			// size | data[i]
			GenUtil.castIfNotAssignable(mh, values);
			mh.callHyphenMethod(MethodType.MEASURE, values);
			// size | elementSize
			mh.visitInsn(LADD);
			// size
		}
		mh.popScope();
		mh.returnOp();
	}

	@Override
	public StringBuilder toFancyString(StringBuilder sb) {
		sb.append(this.getInfo().toFancyString()).append("\n");
		sb.append(" >-> ").append(this.values.toFancyString()).append("\n");
		return sb.append('\n');
	}
}
