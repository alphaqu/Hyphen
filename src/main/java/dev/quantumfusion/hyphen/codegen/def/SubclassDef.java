package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.SerializerHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.util.ArrayUtil;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

public class SubclassDef extends MethodDef {
	private final SerializerDef[] subtypes;

	public SubclassDef(SerializerHandler<?, ?> handler, Clazz clazz, Class<?>[] value) {
		super(handler.codegenHandler, clazz, "SUB{" + clazz + " # " + Arrays.stream(value).map(Class::getSimpleName).collect(Collectors.joining(", ")) + "}");
		this.subtypes = ArrayUtil.map(value, SerializerDef[]::new, subclass ->
				handler.acquireDef(clazz.asSub(subclass))
		);
	}

	@Override
	public void writeMethodGet(MethodHandler mh) {
		
		mh.op(ACONST_NULL, ARETURN);
	}

	@Override
	public void writeMethodPut(MethodHandler mh) {
		mh.op(RETURN);
	}

	@Override
	public void writeMethodMeasure(MethodHandler mh) {
		mh.op(ICONST_0, IRETURN);
	}
}
