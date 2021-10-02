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

import static org.objectweb.asm.Opcodes.*;

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

		if (handler.implementations.containsKey(info.getClazz())) {
			methodMetadata.addField(null, handler.implementations.get(info.getClazz()).apply(info));
			methodMetadata.compile();
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
		this.objects = this.fields;//new LinkedHashMap<>();
		/*
		fields.forEach((field, def) -> {
			if (field != null && isPackable(field.clazz())) primitives.add(PrimEntry.create(field));
			else this.objects.put(field, def);
		});
		this.primitives = PrimGroup.createGroups(primitives, METHODS, 8);*/
		this.primitives = List.of();
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
		if (this.primitives.isEmpty()) {
			if (this.objects.isEmpty()) {
				mh.returnOp();
			} else {
				io.load();
				data.load();
				// io data
				int i = 0;

				for (var entry : this.fields.entrySet()) {
					if (++i < this.fields.size()) {
						mh.visitInsn(DUP2);
						// (io | data |) io | data
					}

					var field = entry.getKey();
					var def = entry.getValue();

					if (field != null) {

						TypeInfo fieldType = field.clazz();
						mh.getField(GETFIELD, this.info.getClazz(), field.name(), fieldType.getRawType());
						if (!fieldType.getClazz().isAssignableFrom(fieldType.getRawType())) {
							mh.cast(fieldType.getClazz());
						}
					}
					// io | field
					def.doPut(mh);
				}

				mh.returnOp();
			}
		} else {
			mh.returnOp();
		}
	}

	@Override
	public void writeGet(MethodHandler mh, MethodHandler.Var io) {
		if (this.primitives.isEmpty()) {
			if (this.objects.isEmpty()) {
				mh.visitInsn(ACONST_NULL);
				mh.returnOp();
			} else if (this.fields.containsKey(null)) {
				SerializerDef serializerDef = this.fields.get(null);
				io.load();
				serializerDef.doGet(mh);
				mh.returnOp();
			} else {
				mh.typeInsn(NEW, this.info.getClazz());
				mh.visitInsn(DUP);
				// OBJECT | OBJECT

				for (var def : this.fields.values()) {
					io.load();
					def.doGet(mh);
				}

				// OBJECT | OBJECT | ... fields
				mh.callSpecialMethod(this.info.getClazz(),
						"<init>",
						null,
						this.fields.keySet()
								.stream()
								.map(FieldEntry::clazz)
								.map(TypeInfo::getRawType)
								.toArray(Class[]::new));
				// OBJECT
				mh.returnOp();
			}
		} else {
			mh.visitInsn(ACONST_NULL);
			mh.returnOp();
		}
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
			if (entries.isEmpty())
				return List.of();
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

	@Override
	public long getSize() {
		boolean dynamic = false;
		int size = 0;
		for (SerializerDef value : this.fields.values()) {
			long fieldSize = value.getSize();
			if (fieldSize < 0) {
				dynamic = true;
				fieldSize = ~fieldSize;
			}
			size += fieldSize;
		}
		return dynamic ? ~size : size;
	}

	@Override
	public void writeMeasure(MethodHandler mh, MethodHandler.Var data) {
		var dynamicFields = this.fields.entrySet().stream().filter(i -> i.getValue().getSize() < 0).toList();

		int i = 0;

		for (var entry : dynamicFields) {
			data.load();
			// (size |) data

			var field = entry.getKey();
			var def = entry.getValue();


			if (field != null) {
				TypeInfo fieldType = field.clazz();
				mh.getField(GETFIELD, this.info.getClazz(), field.name(), fieldType.getRawType());
				if (!fieldType.getClazz().isAssignableFrom(fieldType.getRawType())) {
					mh.cast(fieldType.getClazz());
				}
			}

			// (size |) field
			def.calcSubSize(mh);
			// (size |) nextSize

			if (i++ > 0) {
				// size | nextSize
				mh.visitInsn(LADD);
				// size
			}
		}
	}
}
