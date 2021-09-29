package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.codegen.FieldEntry;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.info.ClassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.thr.exception.HyphenException;
import dev.quantumfusion.hyphen.util.ScanUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.ACONST_NULL;

public class ClassMethod extends MethodMetadata {
	private final Map<FieldEntry, SerializerDef> fields;


	private ClassMethod(TypeInfo info, Map<FieldEntry, SerializerDef> fields) {
		super(info);
		this.fields = fields;
	}

	public static ClassMethod create(ClassInfo info, ScanHandler handler) {
		var methods = handler.methods;
		var implementations = handler.implementations;

		var methodMetadata = new ClassMethod(info, new LinkedHashMap<>());
		methods.put(info, methodMetadata);

		if (implementations.containsKey(info.clazz)) {
			methodMetadata.fields.put(null, implementations.get(info.clazz).apply(info));
			return methodMetadata;
		}

		//get the fields
		var allFields = info.getAllFields(handler);
		//check if it exists / if its accessible
		ScanUtils.checkConstructor(handler, info);
		for (FieldEntry fieldInfo : allFields) {
			try {
				methodMetadata.fields.put(fieldInfo, handler.getDefinition(fieldInfo, info));
			} catch (HyphenException hyphenException) {
				throw hyphenException.addParent(info, fieldInfo.name());
			}
		}

		return methodMetadata;
	}

	public void addField(FieldEntry entry, SerializerDef def) {
		fields.put(entry, def);
	}


	@Override
	public void writePut(MethodHandler mh, MethodHandler.Var io, MethodHandler.Var data) {

		mh.returnOp();
	}

	@Override
	public void writeGet(MethodHandler mh, MethodHandler.Var io) {
		mh.visitInsn(ACONST_NULL);
		mh.returnOp();
	}


}
