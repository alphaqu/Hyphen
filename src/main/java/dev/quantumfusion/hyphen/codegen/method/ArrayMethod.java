package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.info.ArrayInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.io.UnsafeIO;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class ArrayMethod extends MethodMetadata {
	private final TypeInfo values;

	private ArrayMethod(ArrayInfo info) {
		super(info);
		this.values = info.values;
	}

	public static ArrayMethod create(ArrayInfo info, ScanHandler scanHandler) {
		scanHandler.createSerializeMetadata(info.values);
		return new ArrayMethod(info);
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
		// FIXME: NEED TO GET IO HERE
		mh.callInternalStaticMethod("encode_" + this.values.getMethodName(false), null, UnsafeIO.class, this.values.clazz);
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
		mh.visitTypeInsn(ANEWARRAY, Type.getInternalName(this.values.clazz));
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
		// FIXME: NEED TO GET IO HERE
		mh.callInternalStaticMethod("decode_" + this.values.getMethodName(false), this.values.clazz, UnsafeIO.class);
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
}
