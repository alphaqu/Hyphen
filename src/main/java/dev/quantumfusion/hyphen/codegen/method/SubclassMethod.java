package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.codegen.IOHandler;
import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.gen.VarHandler;
import dev.quantumfusion.hyphen.info.SubclassInfo;
import org.objectweb.asm.MethodVisitor;

import java.util.LinkedHashMap;
import java.util.Map;

public class SubclassMethod extends MethodMetadata {
	private final Map<Class<?>, SerializerDef> subtypes = new LinkedHashMap<>();

	public SubclassMethod(SubclassInfo info) {
		super(info);
	}

	public void addSubclass(Class<?> clazz, SerializerDef def) {
		subtypes.put(clazz, def);
	}

	@Override
	public void writePut(MethodVisitor mv, IOHandler io, VarHandler var) {

	}

	@Override
	public void writeGet(MethodVisitor mv, IOHandler io, VarHandler var) {

	}
}
