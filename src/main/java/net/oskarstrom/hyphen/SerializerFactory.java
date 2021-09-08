package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.data.ArrayInfo;
import net.oskarstrom.hyphen.data.ClassInfo;
import net.oskarstrom.hyphen.data.ParameterizedClassInfo;
import net.oskarstrom.hyphen.data.SerializerMethodMetadata;
import net.oskarstrom.hyphen.gen.impl.IntDef;
import net.oskarstrom.hyphen.gen.impl.MethodCallDef;
import net.oskarstrom.hyphen.thr.IllegalClassException;
import net.oskarstrom.hyphen.thr.AccessException;
import net.oskarstrom.hyphen.thr.ThrowHandler;
import net.oskarstrom.hyphen.thr.ClassScanException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static net.oskarstrom.hyphen.thr.ThrowHandler.ThrowEntry.of;

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
			throw ThrowHandler.fatal(IllegalClassException::new, "The Input class has Parameters,");
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
		List<Field> allFields = clazz.getAllFields(field -> field.getDeclaredAnnotation(Serialize.class) != null);
		checkConstructor(allFields, clazz);
		for (Field field : allFields) {
			var classInfo = createClassInfo(clazz, field.getType(), field.getGenericType());
			var def = (ObjectSerializationDef) null;
			if (implementations.containsKey(classInfo.clazz)) {
				def = implementations.get(classInfo.clazz).apply(classInfo);
			} else {
				//check if field is legal
				//we dont do this on the serializerDef because they might do some grandpa 360 no-scopes on fields and access them another way
				checkField(field, clazz);
				scanClass(classInfo);
				def = new MethodCallDef(classInfo);
			}
			methodMetadata.fields.put(field, def);
		}
		methods.put(clazz, methodMetadata);
	}

	private void checkField(Field field, ClassInfo source) {
		int modifiers = field.getModifiers();
		if (Modifier.isPrivate(modifiers)) {
			throw ThrowHandler.fatal(AccessException::new, "Target field is private.",
					of("Field Name", field.getName()),
					of("Field Class", field.getType().getSimpleName()),
					of("Source Class", source.clazz.getName())
			);
		}

		if (Modifier.isProtected(modifiers)) {
			throw ThrowHandler.fatal(AccessException::new, "Target field is protected.",
					of("Field Name", field.getName()),
					of("Field Class", field.getType().getSimpleName()),
					of("Source Class", source.clazz.getName())
			);
		}

		//package-private thx java
		if (!Modifier.isPublic(modifiers)) {
			throw ThrowHandler.fatal(AccessException::new, "Target field is package-private.",
					of("Field Name", field.getName()),
					of("Field Class", field.getType().getSimpleName()),
					of("Source Class", source.clazz.getName())
			);
		}
	}

	private void checkConstructor(List<Field> fields, ClassInfo info) {
		try {
			info.clazz.getConstructor(fields.stream().map(Field::getType).toArray(Class[]::new));
		} catch (NoSuchMethodException e) {
			ThrowHandler.Throwable[] throwables = new ThrowHandler.Throwable[2 + fields.size()];
			throwables[0] = ThrowHandler.ThrowEntry.of("Source Class", info.clazz.getSimpleName());
			throwables[1] = ThrowHandler.ThrowEntry.of("Expected Constructor Parameters","");
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				throwables[i + 2] = ThrowHandler.ThrowEntry.of("\t" + field.getName(), field.getType().getSimpleName());
			}
			throw ThrowHandler.fatal(AccessException::new, "Matching Constructor does not exist", throwables);
		}
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
			//map all of the types,  A<String,Integer> -> B<K,S> == B<K = String, S = Integer>
			LinkedHashMap<String, ClassInfo> typeMap;
			if (source instanceof ParameterizedClassInfo info) {
				typeMap = info.types;
			} else typeMap = new LinkedHashMap<>();
			var classInfo = typeMap.get(typeVariable.getName());

			if (classInfo == null) {
				throw ThrowHandler.fatal(ClassScanException::new, "Type could not be identified",
						of("Source Class", source.clazz.getName()),
						of("Error Class", classType.getName()),
						of("Type Name", typeVariable.getName()),
						of("Type Class", typeVariable.getClass().getSimpleName())
				);
			}
			return classInfo;
		}

		//T[] arrrrrrrr
		if (genericType instanceof GenericArrayType genericArrayType) {
			if (classType == null) {
				throw new RuntimeException("what, i see, a T[]T array yes"); //null below this comment is the thing that can cause this.
			}
			var componentType = genericArrayType.getGenericComponentType();
			var classInfo = createClassInfo(source, classType, componentType);
			if (classInfo == null) {
				throw ThrowHandler.fatal(ClassScanException::new, "Array component could not be identified",
						of("Source Class", source.clazz.getName()),
						of("Error Class", classType.getName()),
						of("Type Name", componentType.getTypeName()),
						of("Type Class", componentType.getClass().getSimpleName())
				);
			}
			return new ArrayInfo(classType, classInfo, this);
		}

		return null;
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
