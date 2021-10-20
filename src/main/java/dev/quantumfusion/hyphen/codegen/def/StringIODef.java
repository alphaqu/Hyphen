package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.io.UnsafeIO;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.objectweb.asm.Opcodes.*;

public class StringIODef implements SerializerDef {
	@Override
	public void writePut(MethodHandler mh, Runnable valueLoad) {
		mh.varOp(ILOAD, "io");
		valueLoad.run();
		mh.callInst(INVOKEVIRTUAL, mh.ioClass, "putString", Void.TYPE, String.class);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.varOp(ILOAD, "io");
		mh.callInst(INVOKEVIRTUAL, mh.ioClass, "getString", String.class);
	}

	@Override
	public void writeMeasure(MethodHandler mh, Runnable valueLoad) {
		if (mh.ioClass == UnsafeIO.class) {
			//TODO unsafeStringMeasure
		} else {
			valueLoad.run();
			// kinda bad for speed, but it's kinda our only option here
			mh.visitFieldInsn(GETSTATIC, StandardCharsets.class, "UTF_8", Charset.class);
			mh.callInst(INVOKEVIRTUAL, String.class, "getBytes", byte[].class, Charset.class);
			mh.op(ARRAYLENGTH, ICONST_4, IADD);
		}
	}
}
