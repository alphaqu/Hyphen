package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

import java.util.Locale;

import static org.objectweb.asm.Opcodes.*;

public class ArrayIODef implements SerializerDef {
	private final Class<?> clazz;

	public ArrayIODef(Class<?> clazz) {
		this.clazz = clazz;

	}

	@Override
	public Class<?> getType() {
		return clazz;
	}

	private String name() {
		String s = this.clazz.componentType().getSimpleName() + "Array";
		return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
	}

	@Override
	public void writePut(MethodHandler mh) {
		mh.visitInsn(DUP2);
		mh.visitInsn(ARRAYLENGTH);
		mh.callIOPut(int.class);
		mh.callInstanceMethod(mh.getIOClazz(), "put" + this.name(), null, this.clazz);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.visitInsn(DUP);
		mh.callIOGet(int.class);
		mh.callInstanceMethod(mh.getIOClazz(), "get" + this.name(), this.clazz, int.class);
	}

	@Override
	public boolean needsFieldOnMeasure() {
		return true;
	}

	@Override
	public void writeMeasure(MethodHandler mh) {
		if (this.clazz == String[].class) {

		} else {
			int size;
			if (this.clazz == boolean.class || this.clazz == byte.class) size = 0;
			else if (this.clazz == char.class || this.clazz == short.class) size = 1;
			else if (this.clazz == int.class || this.clazz == float.class) size = 2;
			else /*if (this.clazz == long.class || this.clazz == double.class)*/ size = 3;

			// data
			mh.visitInsn(ARRAYLENGTH);
			mh.visitInsn(I2L);
			if(size != 0){
				mh.visitLdcInsn(size);
				mh.visitInsn(LSHL);
			}
		}
	}

	@Override
	public StringBuilder toFancyString(StringBuilder sb) {
		return sb.append("IO{").append(this.clazz.getSimpleName()).append("}");
	}
}
