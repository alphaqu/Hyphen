package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.MethodMode;
import dev.quantumfusion.hyphen.info.ArrayInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class ArrayMethod extends MethodMetadata {
	private final TypeInfo values;
	private final MethodMetadata elementSerializer;

	private ArrayMethod(ArrayInfo info, MethodMetadata elementSerializer) {
		super(info);
		this.values = info.values;
		this.elementSerializer = elementSerializer;
	}

	public static ArrayMethod create(ArrayInfo info, ScanHandler scanHandler) {
		MethodMetadata serializeMethod = scanHandler.createSerializeMethod(info.values);
		return new ArrayMethod(info, serializeMethod);
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
	public void writePut(MethodHandler mh, MethodHandler.Var io, MethodHandler.Var data) {
		mh.pushScope();
		io.load();
		data.load();
		// io | data
		mh.visitInsn(ARRAYLENGTH);
		// io | length
		mh.visitInsn(DUP);
		// io | length | length
		var aFor = mh.createForWithLength();
		// io | length
		mh.callIOPut(int.class);
		try (var forLoop = aFor.start()) {
			io.load();
			data.load();
			// io | data
			forLoop.i.load();
			mh.visitInsn(AALOAD);
			// io | data[i]
			if (!this.values.getClazz().isAssignableFrom(this.values.getRawType())) {
				mh.cast(this.values.getClazz());
			}
			mh.callHyphenMethod(MethodMode.PUT, values);
		}

		mh.popScope();
		mh.returnOp();
	}

	@Override
	public void writeGet(MethodHandler mh, MethodHandler.Var io) {
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
		mh.visitTypeInsn(ANEWARRAY, Type.getInternalName(this.values.getClazz()));
		// arr
		array.store();
		try (var forLoop = aFor.start()) {
			array.load();
			// array
			forLoop.i.load();
			// array | i
			io.load();
			// array | i | io
			mh.callHyphenMethod(MethodMode.GET, values);
			// array | i | component
			mh.visitInsn(AASTORE);
		}
		array.load();
		mh.returnOp();
		mh.popScope();
	}

	@Override
	public void writeMeasure(MethodHandler mh, MethodHandler.Var data) {
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
			forLoop.i.load();
			mh.visitInsn(AALOAD);
			// size | data[i]
			if (!this.values.getClazz().isAssignableFrom(this.values.getRawType())) {
				mh.cast(this.values.getClazz());
			}
			mh.callHyphenMethod(MethodMode.MEASURE, values);
			// size | elementSize
			mh.visitInsn(LADD);
			// size
		}

		mh.popScope();
		mh.returnOp();
	}
}
