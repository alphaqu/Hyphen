package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.MethodInfo;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.scan.type.ParaClazz;
import dev.quantumfusion.hyphen.util.GenUtil;

import java.util.Map;
import java.util.function.BiConsumer;

import static org.objectweb.asm.Opcodes.*;

public class MapDef extends MethodDef {
	private final Clazz keyClazz;
	private final Clazz valueClazz;
	private final SerializerDef keyDef;
	private final SerializerDef valueDef;
	private final MethodInfo putLambdaMethod;


	public MapDef(SerializerHandler<?, ?> handler, ParaClazz clazz) {
		super(handler, clazz);
		this.keyClazz = clazz.define("K");
		this.valueClazz = clazz.define("V");
		this.keyDef = handler.acquireDef(this.keyClazz);
		this.valueDef = handler.acquireDef(this.valueClazz);
		this.putLambdaMethod = handler.codegenHandler.createMethodInfo(clazz, "$lambda$put", Void.TYPE, handler.ioClass, this.keyClazz.getBytecodeClass(), this.valueClazz.getBytecodeClass());
	}

	@Override
	protected void writeMethodPut(MethodHandler mh, Runnable valueLoad) {
		valueLoad.run();
		mh.varOp(ILOAD, "io");

		GenUtil.createMethodRef(mh,
								BiConsumer.class, "accept", Void.TYPE, new Class[]{Object.class, Object.class}, // BiConsumer::accept(Object, Object) void
								mh.self, this.putLambdaMethod.getName(), Void.TYPE, new Class[]{mh.ioClass}, new Class[]{this.keyClazz.getBytecodeClass(), this.valueClazz.getBytecodeClass()}
		);

		mh.callInst(INVOKEVIRTUAL, Map.class, "forEach", Void.TYPE, BiConsumer.class);
	}

	@Override
	protected void writeMethodGet(MethodHandler mh) {
		mh.op(ACONST_NULL);
	}

	@Override
	protected void writeMethodMeasure(MethodHandler mh, Runnable valueLoad) {
		mh.op(ICONST_4);
	}

	@Override
	public void writeMethods(CodegenHandler<?, ?> handler, CodegenHandler.MethodWriter writer, boolean spark) {
		super.writeMethods(handler, writer, spark);
		if (!handler.options.get(Options.DISABLE_MEASURE))
			writer.writeMethod(this.clazz, this.putLambdaMethod, false, true,
							   mh -> {
								 keyDef.writePut(mh, () -> mh.varOp(ILOAD, "data"));
								 valueDef.writePut(mh, () -> mh.varOp(ILOAD, "data$"));
							 });
	}
}