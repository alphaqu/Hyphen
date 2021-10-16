package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.util.GenUtil;

import static org.objectweb.asm.Opcodes.*;

public class IODef implements SerializerDef {
	public Class<?> primitive;


	@Override
	public void writePut(MethodHandler mh) {
		mh.visitMethodInsn(INVOKEVIRTUAL, mh.ioClass, "put" + getName(), Void.TYPE, primitive);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.visitMethodInsn(INVOKEVIRTUAL, mh.ioClass, "get" + getName(), primitive, primitive.isArray() ? int.class : Void.TYPE);
	}

	@Override
	public void writeMeasure(MethodHandler mh) {

	}

	private String getName() {
		if (primitive.isArray())
			return GenUtil.upperCase(primitive.getComponentType().getSimpleName()) + "Array";
		return GenUtil.upperCase(primitive.getSimpleName());
	}
}
