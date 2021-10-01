package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.codegen.Constants;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.info.TypeInfo;

import static org.objectweb.asm.Opcodes.LADD;

public abstract class MethodMetadata {
	protected final TypeInfo info;

	public MethodMetadata(TypeInfo info) {
		this.info = info;
	}

	public abstract void writePut(MethodHandler mh, MethodHandler.Var io, MethodHandler.Var data);

	public abstract void writeGet(MethodHandler mh, MethodHandler.Var io);

	public abstract void writeSubCalcSize(MethodHandler mh, MethodHandler.Var data);

	public abstract long getSize();

	public void createCalcSize(MethodHandler mh, MethodHandler.Var data){
		long size = this.getSize();
		if(size >= 0){
			mh.visitLdcInsn(size);
			mh.returnOp();
		} else {
			mh.visitLdcInsn(size);
			this.writeSubCalcSize(mh, data);
			mh.visitInsn(LADD);
		}
	}

	public void callPut(MethodHandler mh){
		mh.callInternalStaticMethod(Constants.PUT_FUNC + this.info.getMethodName(false), null, mh.getIOClazz(), this.info.getClazz());
	}

	public void callGet(MethodHandler mh){
		mh.callInternalStaticMethod(Constants.GET_FUNC + this.info.getMethodName(false), this.info.getClazz(), mh.getIOClazz());
	}

	public void callSubCalcSize(MethodHandler mh){
		mh.callInternalStaticMethod(Constants.SUB_CALC_FUNC + this.info.getMethodName(false), long.class, this.info.getClazz());
	}
}
