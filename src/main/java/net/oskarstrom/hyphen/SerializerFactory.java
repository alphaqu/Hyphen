package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.data.*;
import net.oskarstrom.hyphen.gen.impl.IntDef;
import net.oskarstrom.hyphen.gen.impl.MethodCallDef;
import net.oskarstrom.hyphen.util.Color;
import net.oskarstrom.hyphen.util.ScanUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SerializerFactory {
	@Nullable
	private final DebugHandler debugMode;
	private final ImplMap implMap;
	private final Map<ClassInfo, SerializerMethod> implMethods = new HashMap<>();
	private final SubclassMap subclasses = new SubclassMap();


	protected SerializerFactory(@Nullable DebugHandler debugMode) {
		this.debugMode = debugMode;
		this.implMap = new ImplMap();
	}

	public static SerializerFactory create() {
		return createInternal(false);
	}

	public static SerializerFactory createDebug() {
		return createInternal(true);
	}

	private static SerializerFactory createInternal(boolean debugMode) {
		final SerializerFactory serializerFactory = new SerializerFactory(debugMode ? new DebugHandler() : null);
		serializerFactory.addImpl(int.class, (field) -> new IntDef());
		return serializerFactory;
	}

	public void addImpl(Class<?> clazz, Function<FieldInfo, ObjectSerializationDef> creator) {
		implMap.put(clazz, creator);
	}

	public void addSubclasses(Class<?> clazz, Class<?>... subclasses) {
		this.subclasses.addSubclasses(clazz, subclasses);
	}

	public void build(Class<?> clazz) {
		final ClassInfo sourceClass = ClassInfo.create(null, clazz, null);
		scanClass(sourceClass);
		if (debugMode != null) {
			debugMode.printMethods(implMethods);
		}
	}

	private void scanClass(ClassInfo clazz) {
		//check if a method already exists for this class
		if (implMethods.containsKey(clazz)) {
			clazz.methodName = (implMethods.get(clazz).name);
			return;
		}

		List<ImplDetails> implementation = new ArrayList<>();
		for (FieldInfo field : clazz.getFields(subclasses)) {
			ImplDetails impl = implMap.getFieldImpl(field);
			if (impl == null) {
				scanClass(field);
				impl = implMap.createImplDetails(clazz, field);
			}
			if (field.subclasses != null) {
				for (ClassInfo subclass : field.subclasses)
					scanClass(subclass);
			}
			implementation.add(impl);
		}
		clazz.methodName = clazz.parseMethodName();
		implMethods.put(clazz, new SerializerMethod(implementation, clazz.methodName));
	}


	private static class ImplMap extends HashMap<Class<?>, Function<FieldInfo, ObjectSerializationDef>> {

		public ImplDetails createImplDetails(ClassInfo source, FieldInfo fieldInfo) {
			ObjectSerializationDef out;
			var serializerDefCreator = get(fieldInfo.getClazz());
			if (serializerDefCreator == null) {
				out = new MethodCallDef();
			} else {
				out = serializerDefCreator.apply(fieldInfo);
			}
			return new ImplDetails(out, source, fieldInfo);
		}

		public boolean containsImpl(ClassInfo info) {
			return containsKey(info.getClazz());
		}

		@Nullable
		public ImplDetails getFieldImpl(FieldInfo fieldInfo) {
			if (containsImpl(fieldInfo)) {
				return createImplDetails(fieldInfo.source, fieldInfo);
			}
			return ScanUtils.search(fieldInfo.source, this::containsImpl, classInfo -> createImplDetails(classInfo, fieldInfo));
		}
	}


}
