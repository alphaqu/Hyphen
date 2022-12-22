package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.io.IOBufferInterface;
import dev.quantumfusion.hyphen.scan.annotations.DataBufferType;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.thr.HyphenException;
import org.objectweb.asm.Opcodes;

import java.nio.*;

public class BufferDef implements SerializerDef {
	protected final Class<?> buffer;
	protected final Class<?> primitive;
	protected final BufferType type;

	public BufferDef(Clazz clazz, SerializerHandler<?, ?> serializerHandler) {
		if (!IOBufferInterface.class.isAssignableFrom(serializerHandler.ioClass)) {
			throw new UnsupportedOperationException("IO implementation does not support buffers");
		}
		Class<?> definedClass = clazz.getDefinedClass();
		if (definedClass == ByteBuffer.class) this.primitive = byte.class;
		else if (definedClass == CharBuffer.class) this.primitive = char.class;
		else if (definedClass == ShortBuffer.class) this.primitive = short.class;
		else if (definedClass == IntBuffer.class) this.primitive = int.class;
		else if (definedClass == LongBuffer.class) this.primitive = long.class;
		else if (definedClass == FloatBuffer.class) this.primitive = float.class;
		else if (definedClass == DoubleBuffer.class) this.primitive = double.class;
		else {
			throw new HyphenException("Type Class is not a ByteBuffer", "Use one of java nio bytebuffers");
		}

		this.buffer = clazz.getDefinedClass();
		Object annotationValue = clazz.getAnnotationValue(DataBufferType.class);
		if (annotationValue == null) {
			annotationValue = BufferType.HEAP;
		}
		this.type = (BufferType) annotationValue;
		if (type != BufferType.HEAP && definedClass != ByteBuffer.class) {
			throw new HyphenException("Only ByteBuffer supports Native buffers.", "Use ByteBuffer or make the buffer type HEAP");
		}
	}

	@Override
	public void writePut(MethodHandler mh, Runnable valueLoad) {
		mh.loadIO();
		valueLoad.run();
		mh.op(Opcodes.DUP);
		mh.loadIO();
		mh.op(Opcodes.SWAP);

		// IO | VALUE | IO | VALUE
		mh.callInst(Opcodes.INVOKEVIRTUAL, buffer, "limit", int.class);
		mh.op(Opcodes.DUP_X1);
		// IO | VALUE | INT | IO | INT

		mh.putIO(int.class);
		// IO | VALUE | INT

		mh.callInst(Opcodes.INVOKEVIRTUAL, mh.ioClass, "put" + buffer.getSimpleName(),  Void.TYPE, buffer, int.class);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.loadIO();
		mh.loadIO();
		mh.getIO(int.class);
		mh.op(Opcodes.DUP);
		// IO | LENGTH | LENGTH
		allocateBuffer(mh);
		// IO | LENGTH | BYTEBUFFER
		mh.op(Opcodes.DUP_X2);
		// BYTEBUFFER | IO | LENGTH | BYTEBUFFER
		mh.op(Opcodes.SWAP);
		mh.callInst(Opcodes.INVOKEVIRTUAL, mh.ioClass, "get" + buffer.getSimpleName(), Void.TYPE, buffer, int.class);
		// BYTEBUFFER
	}

	protected void allocateBuffer(MethodHandler mh) {
		switch (type) {
			case HEAP -> mh.callInst(Opcodes.INVOKESTATIC, buffer, "allocate", buffer, int.class);
			case NATIVE -> {
				assert buffer == ByteBuffer.class;
				mh.callInst(Opcodes.INVOKESTATIC, ByteBuffer.class, "allocateDirect", ByteBuffer.class, int.class);
			}
		}
	}

	@Override
	public long getStaticSize() {
		return 4;
	}

	@Override
	public void writeMeasure(MethodHandler mh, Runnable valueLoad) {
		long primitiveSize = PrimitiveIODef.getSize(primitive);

		valueLoad.run();
		mh.callInst(Opcodes.INVOKEVIRTUAL, buffer, "limit", int.class);
		mh.op(Opcodes.I2L);
		if (primitiveSize != 1) {
			mh.visitLdcInsn(primitiveSize);
			mh.op(Opcodes.LMUL);
		}
	}

	public enum BufferType {
		HEAP,
		NATIVE
	}
}
