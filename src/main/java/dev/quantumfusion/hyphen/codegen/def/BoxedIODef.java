package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class BoxedIODef extends IODef {
	protected final Class<?> boxed;

	public BoxedIODef(Class<?> boxed) {
		super(getPrimitiveFromBoxed(boxed));
		this.boxed = boxed;
	}

	@Override
	public void writeGet(MethodHandler mh) {
		super.writeGet(mh);
		mh.visitMethodInsn(INVOKESTATIC, boxed, "valueOf", boxed, primitive);
	}

	@Override
	public void writePut(MethodHandler mh) {
		mh.visitMethodInsn(INVOKEVIRTUAL, boxed, primitive.getSimpleName() + "Value", primitive);
		super.writePut(mh);
	}

	private static Class<?> getPrimitiveFromBoxed(Class<?> boxed) {
		try {
			return (Class<?>) boxed.getDeclaredField("TYPE").get(null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}