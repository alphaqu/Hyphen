package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.data.ArrayInfo;
import net.oskarstrom.hyphen.data.ClassInfo;
import net.oskarstrom.hyphen.data.ParameterizedClassInfo;
import net.oskarstrom.hyphen.data.SerializerMethodMetadata;
import net.oskarstrom.hyphen.gen.impl.IntDef;
import net.oskarstrom.hyphen.gen.impl.MethodCallDef;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class SerializerFactory {
	@Nullable
	private final DebugHandler debugHandler;
	private final Map<ClassInfo, SerializerMethodMetadata> methods = new HashMap<>();
	private final Map<Class<?>, Function<ClassInfo, ObjectSerializationDef>> implementations = new HashMap<>();


	protected SerializerFactory(@Nullable DebugHandler debugMode) {
		this.debugHandler = debugMode;
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

	public void addImpl(Class<?> clazz, Function<ClassInfo, ObjectSerializationDef> creator) {
		implementations.put(clazz, creator);
	}

	public void build(Class<?> clazz) {
		if (clazz.getTypeParameters().length > 0) {
			throw new RuntimeException("Data class can not have any type parameters, as it is impossible to trace");
		}

		scanClass(new ClassInfo(clazz, this));

		if (debugHandler != null) {
			debugHandler.printMethods(methods);
		}
	}

	private void scanClass(ClassInfo clazz) {
		//check if a method already exists for this class
		if (methods.containsKey(clazz)) {
			return;
		}

		SerializerMethodMetadata methodMetadata = new SerializerMethodMetadata(clazz);
		for (Field field : clazz.getAllFields(field -> field.getDeclaredAnnotation(Serialize.class) != null)) {
			var classInfo = createClassInfo(clazz, field.getType(), field.getGenericType());
			var def = (ObjectSerializationDef) null;
			if (implementations.containsKey(classInfo.clazz)) {
				def = implementations.get(classInfo.clazz).apply(classInfo);
			} else {
				scanClass(classInfo);
				def = new MethodCallDef(classInfo);
			}
			methodMetadata.fields.put(field, def);
		}
		methods.put(clazz, methodMetadata);
	}


	protected ClassInfo createClassInfo(ClassInfo source, Class<?> classType, Type genericType) {
		//Object / int / Object[] / int[]
		if (genericType instanceof Class clazz) {
			return new ClassInfo(clazz, this);
		}

		//Thing<T,T>
		if (genericType instanceof ParameterizedType type) {
			LinkedHashMap<String, ClassInfo> out = mapTypes(source, type);
			return new ParameterizedClassInfo((Class<?>) type.getRawType(), out, this);
		}

		//T thing
		if (genericType instanceof TypeVariable typeVariable) {
			LinkedHashMap<String, ClassInfo> typeMap;
			if (source instanceof ParameterizedClassInfo info) {
				typeMap = info.types;
			} else typeMap = new LinkedHashMap<>();
			return typeMap.get(typeVariable.getName());
		}

		//T[] arrrrrrrr
		if (genericType instanceof GenericArrayType genericArrayType) {
			if (classType == null) {
				throw new RuntimeException("what, i see, a T[]T array yes");//null below this comment is the thing that can cause this.
			}
			return new ArrayInfo(classType, createClassInfo(source, null, genericArrayType.getGenericComponentType()), this);
		}

		throw new UnsupportedOperationException(genericType.getClass() + " lol");
	}

	private LinkedHashMap<String, ClassInfo> mapTypes(ClassInfo source, ParameterizedType type) {
		var out = new LinkedHashMap<String, ClassInfo>();

		var clazz = (Class<?>) type.getRawType();
		var innerTypes = clazz.getTypeParameters();
		var parameters = type.getActualTypeArguments();
		for (int i = 0; i < parameters.length; i++) {
			out.put(innerTypes[i].getName(), createClassInfo(source, clazz, parameters[i]));
		}
		return out;
	}


}
