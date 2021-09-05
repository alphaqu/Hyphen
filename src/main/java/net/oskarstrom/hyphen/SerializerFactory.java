package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.data.ClassInfo;
import net.oskarstrom.hyphen.data.FieldInfo;
import net.oskarstrom.hyphen.data.ImplDetails;
import net.oskarstrom.hyphen.data.SerializerMethod;
import net.oskarstrom.hyphen.gen.impl.IntDef;
import net.oskarstrom.hyphen.gen.impl.MethodCallDef;
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

	public void build(Class<?> clazz) {
		final ClassInfo sourceClass = new ClassInfo(clazz);
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
		for (FieldInfo field : clazz.getFields()) {
			ImplDetails impl = implMap.getFieldImpl(field);
			if (impl == null) {
				scanClass(field);
				impl = implMap.createImplDetails(clazz, field);
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
			return searchSubclasses(fieldInfo.source, fieldInfo);
		}

		@Nullable
		private ImplDetails searchSubclasses(ClassInfo classInfo, FieldInfo fieldInfo) {
			final ClassInfo[] subClasses = classInfo.getSuperAndInterfaces();
			if (subClasses != null) {
				for (ClassInfo info : subClasses) {
					if (containsImpl(info)) {
						return createImplDetails(info, fieldInfo);
					}
				}

				//if no implementations scan deeper to see if interface / superclass has impl
				for (ClassInfo info : subClasses) {
					final ImplDetails impl = searchSubclasses(info, fieldInfo);
					if (impl != null) {
						return impl;
					}
				}
			}

			return null;
		}

	}


}
