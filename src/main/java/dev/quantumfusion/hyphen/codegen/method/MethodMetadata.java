package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.Constants;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.info.TypeInfo;

import static org.objectweb.asm.Opcodes.*;

public abstract class MethodMetadata {
	protected final TypeInfo info;

	public MethodMetadata(TypeInfo info) {
		this.info = info;
	}

	public TypeInfo getInfo() {
		return this.info;
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

	public void createPut(CodegenHandler ch){
		try (MethodHandler mh = MethodHandler.createVoid(
				ch,
				ACC_STATIC | ACC_PUBLIC | ACC_FINAL,
				Constants.PUT_FUNC + this.getInfo().getMethodName(false),
				ch.getIOMode().ioClass,
				this.getInfo().getClazz())
		) {

			MethodHandler.Var io;
			MethodHandler.Var data;
			io = mh.createVar("io", ch.getIOMode().ioClass);
			data = mh.createVar("data", this.getInfo().getClazz());
			this.writePut(mh, io, data);
		}
	}

	public void createGet(CodegenHandler ch){
		final boolean main = false;
		try (MethodHandler mh = MethodHandler.create(
				ch,
				(main ? 0 : ACC_STATIC) | ACC_PUBLIC | ACC_FINAL,
				Constants.GET_FUNC + this.getInfo().getMethodName(false),
				this.getInfo().getClazz(),
				ch.getIOMode().ioClass)) {
			MethodHandler.Var io;
			io = mh.createVar("io", ch.getIOMode().ioClass);

			this.writeGet(mh, io);
		}
	}

	public void createSubCalc(CodegenHandler ch) {
		if(this.getSize() >= 0) return; // skip

		try (MethodHandler mh = MethodHandler.create(
				ch,
				ACC_STATIC | ACC_PUBLIC | ACC_FINAL,
				Constants.SUB_CALC_FUNC + this.getInfo().getMethodName(false),
				long.class,
				this.getInfo().getClazz())
		) {
			MethodHandler.Var data;
			data = mh.createVar("data", this.getInfo().getClazz());
			this.writeSubCalcSize(mh, data);
		}
	}
}
