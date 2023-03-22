package dev.notalpha.hyphen.codegen.def;

import dev.notalpha.hyphen.codegen.MethodWriter;
import dev.notalpha.hyphen.io.UnsafeIO;
import dev.notalpha.hyphen.scan.struct.ClassStruct;
import dev.notalpha.hyphen.scan.struct.Struct;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.objectweb.asm.Opcodes.*;

public class StringIODef extends SerializerDef<Struct> {
	public StringIODef() {
		super(new ClassStruct(String.class));
	}

	@Override
	public void writePut(MethodWriter mh, Runnable valueLoad) {
		mh.loadIO();
		valueLoad.run();
		mh.callInst(INVOKEVIRTUAL, mh.ioClass, "putString", Void.TYPE, String.class);
	}

	@Override
	public void writeGet(MethodWriter mh) {
		mh.loadIO();
		mh.callInst(INVOKEVIRTUAL, mh.ioClass, "getString", String.class);
	}

	@Override
	public void writeMeasure(MethodWriter mh, Runnable valueLoad) {
		valueLoad.run();
		if (mh.ioClass == UnsafeIO.class) {
			mh.callInst(INVOKESTATIC, UnsafeIO.class, "getStringBytes", int.class, String.class);
		} else {
			// kinda bad for speed, but it's kinda our only option here
			mh.visitFieldInsn(GETSTATIC, StandardCharsets.class, "UTF_8", Charset.class);
			mh.callInst(INVOKEVIRTUAL, String.class, "getBytes", byte[].class, Charset.class);
			mh.op(ARRAYLENGTH, I2L);
			mh.visitLdcInsn(4L);
			mh.op(LADD);
		}
	}
}
