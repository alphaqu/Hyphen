package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.codegen.IOHandler;
import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.gen.FieldEntry;
import dev.quantumfusion.hyphen.gen.VarHandler;
import dev.quantumfusion.hyphen.info.ClassInfo;
import org.objectweb.asm.MethodVisitor;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClassMethod extends MethodMetadata {
	private final Map<FieldEntry, SerializerDef> fields = new LinkedHashMap<>();

	public ClassMethod(ClassInfo info) {
		super(info);
	}

	public void addField(FieldEntry entry, SerializerDef def) {
		fields.put(entry, def);
	}


	@Override
	public void writePut(MethodVisitor mv, IOHandler io, VarHandler var) {

	}

	@Override
	public void writeGet(MethodVisitor mv, IOHandler io, VarHandler var) {

	}


}
