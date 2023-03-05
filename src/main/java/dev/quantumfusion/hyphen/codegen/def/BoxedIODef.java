package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodWriter;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class BoxedIODef extends PrimitiveIODef {
	protected final Class<?> boxed;

	public BoxedIODef(Class<?> boxed) {
		super(getPrimitiveFromBoxed(boxed));
		this.boxed = boxed;
	}

	private static Class<?> getPrimitiveFromBoxed(Class<?> boxed) {
		try {
			return (Class<?>) boxed.getDeclaredField("TYPE").get(null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("sus");
		}
	}

	@Override
	public void writeGet(MethodWriter mh) {
		super.writeGet(mh);
		mh.callInst(INVOKESTATIC, boxed, "valueOf", boxed, primitive);
	}

	@Override
	public void writePut(MethodWriter mh, Runnable valueLoad) {
		super.writePut(mh, () -> {
			valueLoad.run();
			mh.callInst(INVOKEVIRTUAL, boxed, primitive.getSimpleName() + "Value", primitive);
		});
	}
}
