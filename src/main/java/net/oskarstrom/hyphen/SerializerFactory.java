package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.annotation.*;
import net.oskarstrom.hyphen.data.*;
import net.oskarstrom.hyphen.gen.impl.AbstractDef;
import net.oskarstrom.hyphen.gen.impl.IntDef;
import net.oskarstrom.hyphen.gen.impl.MethodCallDef;
import net.oskarstrom.hyphen.options.*;
import net.oskarstrom.hyphen.thr.IllegalClassException;
import net.oskarstrom.hyphen.thr.ThrowHandler;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SerializerFactory {
	@Nullable
	private final DebugHandler debugHandler;
	private final Map<ClassInfo, SerializerMethodMetadata> methods = new HashMap<>();
	private final Map<Class<?>, Function<ClassInfo, ObjectSerializationDef>> implementations = new HashMap<>();
	private final Map<Class<? extends Annotation>, OptionParser<?>> hyphenAnnotations = new AnnotationParser.AnnotationOptionMap<>();


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
		serializerFactory.addImpl(Integer.class, (field) -> new AbstractDef() {
			@Override
			public Class<?> getType() {
				return Integer.class;
			}

			@Override
			public String toString() {
				return "BoxedIntegerDef";
			}
		});
		serializerFactory.addImpl(Float.class, (field) -> new AbstractDef() {
			@Override
			public Class<?> getType() {
				return Integer.class;
			}

			@Override
			public String toString() {
				return "BoxedFloatDef";
			}
		});
		serializerFactory.addOption(SerNull.class, new ExistsOption());
		serializerFactory.addOption(SerSubclasses.class, new ArrayOption<>(SerSubclasses::value));
		serializerFactory.addOption(SerComplexSubClass.class, new SimpleAnnotationOption<>());
		serializerFactory.addOption(SerComplexSubClasses.class, new ArrayOption<>(SerComplexSubClasses::value));
		return serializerFactory;
	}

	public void addImpl(Class<?> clazz, Function<ClassInfo, ObjectSerializationDef> creator) {
		implementations.put(clazz, creator);
	}

	public void addOption(Class<? extends Annotation> annotationClass, OptionParser<?> option) {
		hyphenAnnotations.put(annotationClass, option);
	}

	public void build(Class<?> clazz) {
		if (clazz.getTypeParameters().length > 0) {
			throw ThrowHandler.fatal(IllegalClassException::new, "The Input class has Parameters,");
		}

		scanClass(new ClassInfo(clazz, AnnotationParser.parseAnnotations(null, hyphenAnnotations), this));

		if (debugHandler != null) {
			debugHandler.printMethods(methods);
		}
	}

	private void scanClass(ClassInfo clazz) {
		//check if a method already exists for this class
		if (methods.containsKey(clazz)) {
			return;
		}

		var methodMetadata = new SerializerMethodMetadata(clazz);
		methods.put(clazz, methodMetadata);

		//get the fields
		var allFields = clazz.getAllFields(field -> field.getDeclaredAnnotation(Serialize.class) != null);
		//check if it exists / if its accessible
		checkConstructor(allFields, clazz);
		for (FieldMetadata fieldInfo : allFields) {
			var field = fieldInfo.field;
			var classInfo = createClassInfo(clazz, field.getType(), field.getGenericType(), field.getAnnotatedType());
			var def = (ObjectSerializationDef) null;
			if (implementations.containsKey(classInfo.clazz)) {
				def = implementations.get(classInfo.clazz).apply(classInfo);
			} else {
				//check if field is legal
				//we don't do this on the serializerDef because they might do some grandpa 360 no-scopes on fields and access them another way
				ThrowHandler.checkAccess(field.getModifiers(), () -> ThrowHandler.fieldAccessFail(field, clazz));
				scanClass(classInfo);
				def = new MethodCallDef(classInfo);
			}
			methodMetadata.fields.put(field, def);
		}
	}
	private void checkConstructor(List<FieldMetadata> fields, ClassInfo source) {
		try {
			Constructor<?> constructor = source.clazz.getDeclaredConstructor(fields.stream().map(fieldInfo -> fieldInfo.field.getType()).toArray(Class[]::new));
			ThrowHandler.checkAccess(constructor.getModifiers(), () -> ThrowHandler.constructorAccessFail(constructor, source));
		} catch (NoSuchMethodException e) {
			throw ThrowHandler.constructorNotFoundFail(fields, source);
		}
	}


	protected ClassInfo createClassInfo(ClassInfo source, Class<?> classType, Type genericType, @Nullable AnnotatedType annotatedType) {
		var options = AnnotationParser.parseAnnotations(annotatedType, hyphenAnnotations);
		//Object / int / Object[] / int[]

		if (genericType instanceof Class clazz) {
			return new ClassInfo(clazz, options, this);
		}


		//Thing<T,T>
		if (genericType instanceof ParameterizedType type) {
			if (annotatedType instanceof AnnotatedParameterizedType parameterizedType) {
				LinkedHashMap<String, ClassInfo> out = mapTypes(source, type, parameterizedType);
				return new ParameterizedClassInfo((Class<?>) type.getRawType(), options, this, out);
			}
			throw new RuntimeException();
		}

		//T thing
		if (genericType instanceof TypeVariable typeVariable) {

			if (source instanceof ParameterizedClassInfo info) {
				ClassInfo classInfo = info.types.get(typeVariable.getName());
				if (classInfo != null) {
					// safety first!
					// kropp: why are we copying?
					return classInfo.copy();
				}
			}

			throw ThrowHandler.typeFail("Type could not be identified", source, classType, typeVariable);
		}

		//T[] arrrrrrrr
		if (genericType instanceof GenericArrayType genericArrayType) {
			//get component class
			if (annotatedType instanceof AnnotatedArrayType annotatedArrayType) {
				var componentType = genericArrayType.getGenericComponentType();
				var classInfo = createClassInfo(source, classType, componentType, annotatedArrayType.getAnnotatedGenericComponentType());
				if (classInfo == null) {
					throw ThrowHandler.typeFail("Array component could not be identified", source, classType, componentType);
				}
				return new ArrayInfo(classType, options, this, classInfo);
			}
			throw new RuntimeException();
		}

		return null;
	}

	//map all of the types,  A<String,Integer> -> B<K,S> == B<K = String, S = Integer>
	private LinkedHashMap<String, ClassInfo> mapTypes(ClassInfo source, ParameterizedType type, AnnotatedParameterizedType annotatedType) {
		var out = new LinkedHashMap<String, ClassInfo>();
		var clazz = (Class<?>) type.getRawType();
		var innerTypes = clazz.getTypeParameters();
		var annotatedParameters = annotatedType.getAnnotatedActualTypeArguments();
		var parameters = type.getActualTypeArguments();
		for (int i = 0; i < parameters.length; i++) {
			out.put(innerTypes[i].getName(), createClassInfo(source, clazz, parameters[i], annotatedParameters[i]));
		}
		return out;
	}
}
