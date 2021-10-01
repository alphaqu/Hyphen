package dev.quantumfusion.hyphen.codegen.method;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.annotation.SerNull;
import dev.quantumfusion.hyphen.codegen.FieldEntry;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.def.SerializerDef;
import dev.quantumfusion.hyphen.info.ClassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.thr.exception.HyphenException;
import dev.quantumfusion.hyphen.util.ScanUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.ACONST_NULL;

public class ClassMethod extends MethodMetadata {
	private static final int[] METHODS = new int[]{
			8, 4, 2, 1
	};
	private final Map<FieldEntry, SerializerDef> fields;
	private List<PrimGroup> primitives;
	private Map<FieldEntry, SerializerDef> objects;

	private ClassMethod(TypeInfo info, Map<FieldEntry, SerializerDef> fields) {
		super(info);
		this.fields = fields;
	}

	public static ClassMethod create(ClassInfo info, ScanHandler handler) {
		var methodMetadata = new ClassMethod(info, new LinkedHashMap<>());
		handler.methods.put(info, methodMetadata);

		if (handler.implementations.containsKey(info.clazz)) {
			methodMetadata.addField(null, handler.implementations.get(info.clazz).apply(info));
			return methodMetadata;
		}

		//check if it exists / if its accessible
		ScanUtils.checkConstructor(handler, info);
		for (FieldEntry fieldInfo : info.getAllFields(handler)) {
			try {
				methodMetadata.addField(fieldInfo, handler.getDefinition(fieldInfo, info));
			} catch (HyphenException hyphenException) {
				throw hyphenException.addParent(info, fieldInfo.name());
			}
		}

		methodMetadata.compile();
		return methodMetadata;
	}

	public void compile() {
		final List<PrimEntry> primitives = new ArrayList<>();
		this.objects = new LinkedHashMap<>();
		fields.forEach((field, def) -> {
			if (isPackable(field.clazz())) primitives.add(PrimEntry.create(field));
			else this.objects.put(field, def);
		});
		this.primitives = PrimGroup.createGroups(primitives, METHODS, 8);
	}

	public boolean isPackable(TypeInfo typeInfo) {
		final Class<?> clazz = typeInfo.getClazz();
		if (typeInfo.getAnnotation(SerNull.class) == null)
			return clazz == byte.class || clazz == Byte.class || clazz == char.class || clazz == Character.class || clazz == short.class || clazz == Short.class || clazz == int.class || clazz == Integer.class || clazz == float.class || clazz == Float.class || clazz == long.class || clazz == Long.class || clazz == double.class || clazz == Double.class;
		return false;
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

	private record PrimEntry(FieldEntry fieldEntry, int size) {
		public static PrimEntry create(FieldEntry fieldEntry) {
			final int size;
			final Class<?> clazz = fieldEntry.clazz().getClazz();
			if (clazz == byte.class || clazz == Byte.class) size = 1;
			else if (clazz == short.class || clazz == Short.class || clazz == char.class || clazz == Character.class)
				size = 2;
			else if (clazz == int.class || clazz == Integer.class || clazz == float.class || clazz == Float.class)
				size = 4;
			else if (clazz == long.class || clazz == Long.class || clazz == double.class || clazz == Double.class)
				size = 8;
			else throw new RuntimeException();
			return new PrimEntry(fieldEntry, size);
		}
	}

	private record PrimGroup(List<PrimEntry> entries, int size) {

		public static List<PrimGroup> createGroups(List<PrimEntry> entries, int[] methods, int size) {
			var construct = construct(entries, size);
			var lastIndex = construct.size() - 1;
			var last = construct.get(lastIndex).size;
			for (int method : methods) if (last == method) return construct;
			construct.addAll(createGroups(construct.remove(lastIndex).entries, methods, size >> 1));
			return construct;
		}

		private static List<PrimGroup> construct(List<PrimEntry> list, int maxSize) {
			var out = new ArrayList<PrimGroup>();
			while (!list.isEmpty()) {
				int currentSize = 0;
				var currentGroup = new ArrayList<PrimEntry>();
				for (int i = 0; i < list.size(); i++) {
					final int size = list.get(i).size;
					if (size + currentSize <= maxSize) {
						currentSize += size;
						currentGroup.add(list.remove(i--));
					}
				}
				out.add(new PrimGroup(currentGroup, currentSize));
			}
			return out;
		}
	}


}
