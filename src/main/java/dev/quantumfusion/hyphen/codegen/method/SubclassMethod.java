package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.info.SubclassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.ACONST_NULL;

public class SubclassMethod extends MethodMetadata {
	private final Map<Class<?>, SerializerDef> subtypes = new LinkedHashMap<>();

	public SubclassMethod(SubclassInfo info) {
		super(info);
	}

	public static SubclassMethod create(SubclassInfo info, ScanHandler handler) {
		var methodMetadata = new SubclassMethod(info);
		final Map<Class<?>, SerializerDef> subtypes = methodMetadata.subtypes;
		for (TypeInfo subTypeInfo : info.classInfos) {
			if (subtypes.containsKey(subTypeInfo.clazz)) {
				// TODO: throw error, cause there is a duplicated class
				//		 or should this be done earlier
			}

			subtypes.put(subTypeInfo.clazz, handler.getDefinition(null, subTypeInfo, null));
		}
		return methodMetadata;
	}

	public void addSubclass(Class<?> clazz, SerializerDef def) {
		subtypes.put(clazz, def);
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
