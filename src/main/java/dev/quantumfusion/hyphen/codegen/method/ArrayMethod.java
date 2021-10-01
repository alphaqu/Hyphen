package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.codegen.Constants;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.info.ArrayInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import org.objectweb.asm.Label;
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
	public void writePut(MethodHandler mh, MethodHandler.Var io, MethodHandler.Var data) {
		mh.pushScope();
		var length = mh.createVar("length", int.class);
		var i = mh.createVar("i", int.class);

		io.load();
		data.load();
		// io | data
		mh.visitInsn(DUP2);
		// io | data | io | data
		mh.visitInsn(ARRAYLENGTH);
		// io | data | io | length
		mh.visitInsn(DUP);
		// io | data | io | length | length
		length.store();
		// io | data | io | length
		mh.callIOPut(int.class);
		// io | data

		mh.visitInsn(ICONST_0);
		i.store();
		// io | data

		var start = new Label();
		var end = new Label();

		mh.visitLabel(start);
		// io | data
		i.load();
		length.load();
		// io | data | i | length
		mh.visitJumpInsn(IF_ICMPGE, end);
		// io | data
		mh.visitInsn(DUP2);
		// io | data | io | data
		i.load();
		// io | data | io | data | i
		mh.visitInsn(AALOAD);
		// io | data | io | data[i]
		mh.callInternalStaticMethod(Constants.PUT_FUNC + this.values.getMethodName(false), null, mh.getIOClazz(), this.values.getClazz());
		// io | data
		i.iinc(1); // i++
		mh.visitJumpInsn(GOTO, start);

		mh.visitLabel(end);
		// io | data
		mh.visitInsn(POP2);


		mh.popScope();
		mh.returnOp();
	}

	@Override
	public void writeGet(MethodHandler mh, MethodHandler.Var io) {
		mh.pushScope();
		var length = mh.createVar("length", int.class);
		var i = mh.createVar("i", int.class);
		var array = mh.createVar("array", int.class);

		io.load();
		// io
		mh.callIOGet(int.class);
		// length
		mh.visitInsn(DUP);
		// length | length
		length.store();
		// length
		mh.visitTypeInsn(ANEWARRAY, Type.getInternalName(this.values.getClazz()));
		// array
		mh.visitInsn(ICONST_0);
		i.store(); // int i = 0
		// array

		var start = new Label();
		var end = new Label();

		mh.visitLabel(start);
		// array
		i.load();
		length.load();
		// array | i | length
		mh.visitJumpInsn(IF_ICMPGE, end);
		// array
		mh.visitInsn(DUP);
		// array | array
		i.load();
		// array | array | i
		io.load();
		// array | array | i | io
		mh.callInternalStaticMethod(Constants.GET_FUNC + this.values.getMethodName(false), this.values.getClazz(), mh.getIOClazz());
		// array | array | i | component
		mh.visitInsn(AASTORE);
		// array
		i.iinc(1); // i++
		mh.visitJumpInsn(GOTO, start);

		mh.visitLabel(end);
		// array

		mh.returnOp();
		mh.popScope();
	}

	@Override
	public long getSize() {
		return ~4; // length
	}

	@Override
	public void writeSubCalcSize(MethodHandler mh, MethodHandler.Var data) {
		long elementSize = this.elementSerializer.getSize();

		boolean dynamic = elementSize >= 0;
		if (dynamic) elementSize = ~elementSize;

		if(elementSize > 0){
			// fixed size part

			data.load();
			// data
			mh.visitInsn(ARRAYLENGTH);
			// length
			if((elementSize & -elementSize) == elementSize){
				// power of 2
				int shift = Long.numberOfTrailingZeros(elementSize);
				mh.visitLdcInsn(shift);
				mh.visitInsn(LSHL);
			} else {
				mh.visitLdcInsn(elementSize);
				mh.visitInsn(LMUL);
			}
		} else {
			mh.visitInsn(LCONST_0);
		}

		if(dynamic){
			// have to check each value
			mh.pushScope();
			var length = mh.createVar("length", int.class);
			var i = mh.createVar("i", int.class);

			data.load();
			// size | data
			mh.visitInsn(ARRAYLENGTH);
			// size | length
			length.store();
			// size

			mh.visitInsn(ICONST_0);
			i.store();
			// size

			var start = new Label();
			var end = new Label();

			if(elementSize == 0)
				mh.visitInsn(ICONST_0);

			mh.visitLabel(start);
			// size
			i.load();
			length.load();
			// size | i | length
			mh.visitJumpInsn(IF_ICMPGE, end);
			// size
			data.load();
			// size | data
			i.load();
			// size | data | i
			mh.visitInsn(AALOAD);
			// size | data[i]
			this.elementSerializer.callSubCalcSize(mh);
			// size | elementSize
			mh.visitInsn(LADD);
			i.iinc(1); // i++
			mh.visitJumpInsn(GOTO, start);

			mh.visitLabel(end);
			// size
			mh.popScope();
		}

		mh.returnOp();
	}
}
