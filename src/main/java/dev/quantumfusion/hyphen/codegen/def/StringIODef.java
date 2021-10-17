package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.io.UnsafeIO;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.objectweb.asm.Opcodes.*;

public class StringIODef implements SerializerDef {
	@Override
	public void writePut(MethodHandler mh, Runnable alloc) {
		alloc.run();
		mh.callInst(INVOKEVIRTUAL, mh.ioClass, "putString", Void.TYPE, String.class);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.varOp(ILOAD, "io");
		mh.callInst(INVOKEVIRTUAL, mh.ioClass, "getString", String.class);
	}

	@Override
	public void writeMeasure(MethodHandler mh, Runnable alloc) {
		if (mh.ioClass == UnsafeIO.class) {
			//TODO unsafeStringMeasure
		} else {
			alloc.run();
			// kinda bad for speed, but it's kinda our only option here
			mh.visitFieldInsn(GETSTATIC, StandardCharsets.class, "UTF_8", Charset.class);
			mh.callInst(INVOKESTATIC, String.class, "getBytes", byte[].class, Charset.class);
			mh.op(ARRAYLENGTH);
		}
	}
}
