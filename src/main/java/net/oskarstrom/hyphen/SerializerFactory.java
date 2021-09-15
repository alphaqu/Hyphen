package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.annotation.*;
import net.oskarstrom.hyphen.data.*;
import net.oskarstrom.hyphen.gen.impl.AbstractDef;
import net.oskarstrom.hyphen.gen.impl.IntDef;
import net.oskarstrom.hyphen.gen.impl.MethodCallDef;
import net.oskarstrom.hyphen.options.*;
import net.oskarstrom.hyphen.thr.ClassScanException;
import net.oskarstrom.hyphen.thr.IllegalClassException;
import net.oskarstrom.hyphen.thr.ThrowHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;

public class SerializerFactory {
	@Nullable
	private final DebugHandler debugHandler;
	private final Map<TypeInfo, SerializerMetadata> methods = new HashMap<>();
	private final Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> implementations = new HashMap<>();
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
		serializerFactory.addTestImpl(Integer.class, Float.class, ArrayList.class, LinkedList.class);
		serializerFactory.addOption(SerNull.class, new ExistsOption());
		serializerFactory.addOption(SerSubclasses.class, new ArrayOption<>(SerSubclasses::value));
		serializerFactory.addOption(SerComplexSubClass.class, new SimpleAnnotationOption<>());
		serializerFactory.addOption(SerComplexSubClasses.class, new ArrayOption<>(SerComplexSubClasses::value));
		return serializerFactory;
	}

	public void addImpl(Class<?> clazz, Function<? super TypeInfo, ? extends ObjectSerializationDef> creator) {
		this.implementations.put(clazz, creator);
	}

	public void addTestImpl(Class<?>... clazz) {
		for (Class<?> aClass : clazz) {
			this.addTestImpl(aClass);
		}
	}

	public void addTestImpl(Class<?> clazz) {
		this.addImpl(clazz, (field) -> new AbstractDef() {
			@Override
			public Class<?> getType() {
				return clazz;
			}

			@Override
			public String toString() {
				return "FakeTestDef" + clazz.getSimpleName();
			}
		});
	}

	public void addOption(Class<? extends Annotation> annotationClass, OptionParser<?> option) {
		this.hyphenAnnotations.put(annotationClass, option);
	}

	public void build(Class<?> clazz) {
		if (clazz.getTypeParameters().length > 0) {
			throw ThrowHandler.fatal(IllegalClassException::new, "The Input class has Parameters,");
		}

		createClassSerializerMetadata(new ClassInfo(clazz, AnnotationParser.parseAnnotations(null, hyphenAnnotations), this));

		if (debugHandler != null) {
			debugHandler.printMethods(methods);
		}
	}

	private void createSerializeMetadata(TypeInfo typeInfo) {
		if (this.methods.containsKey(typeInfo)) {
			return;
		}

		if (typeInfo instanceof PolymorphicTypeInfo polymorphicTypeInfo) {
			this.createJunctionSerializeMetadata(polymorphicTypeInfo);
		} else if (typeInfo instanceof ClassInfo classInfo) {
			this.createClassSerializerMetadata(classInfo);
		} else if (typeInfo instanceof TypeClassInfo typeClassInfo) {
			this.createSerializeMetadata(typeClassInfo.actual);
		} else {
			throw new IllegalArgumentException("uwu");
		}
	}

	private void createJunctionSerializeMetadata(PolymorphicTypeInfo typeInfo) {
		var methodMetadata = new JunctionSerializerMetadata(typeInfo);
		this.methods.put(typeInfo, methodMetadata);

		var subTypeMap = methodMetadata.subtypes;

		for (TypeInfo subTypeInfo : typeInfo.classInfos) {
			if (subTypeMap.containsKey(subTypeInfo.clazz)) {
				// TODO: throw error, cause there is a duplicated class
				//		 or should this be done earlier
			}

			this.createSerializeMetadata(subTypeInfo);
			subTypeMap.put(subTypeInfo.clazz, this.methods.get(subTypeInfo));
		}
	}

	private void createClassSerializerMetadata(ClassInfo clazz) {
		//check if a method already exists for this class
		if (this.methods.containsKey(clazz)) {
			return;
		}


		var methodMetadata = new ClassSerializerMetadata(clazz);
		this.methods.put(clazz, methodMetadata);

		if (this.implementations.containsKey(clazz.clazz)) {
			methodMetadata.fields.put(null, this.implementations.get(clazz.clazz).apply(clazz));
			return;
		}

		//get the fields
		var allFields = clazz.getAllFields(field -> field.getDeclaredAnnotation(Serialize.class) != null);
		//check if it exists / if its accessible
		checkConstructor(allFields, clazz);
		for (FieldMetadata fieldInfo : allFields) {
			var def = this.getDefinition(fieldInfo, clazz);
			methodMetadata.fields.put(fieldInfo, def);
		}
	}

	private ObjectSerializationDef getDefinition(FieldMetadata field, ClassInfo source) {
		var classInfo = field.clazz;
		if (!(classInfo instanceof PolymorphicTypeInfo) && implementations.containsKey(classInfo.clazz)) {
			return implementations.get(classInfo.clazz).apply(classInfo);
		} else {
			//check if field is legal
			//we don't do this on the serializerDef because they might do some grandpa 360 no-scopes on fields and access them another way
			ThrowHandler.checkAccess(field.modifier, () -> ThrowHandler.fieldAccessFail(field, source));

			this.createSerializeMetadata(classInfo);
			return new MethodCallDef(classInfo);
		}
	}

	private PolymorphicTypeInfo createPolymorphicClass(ClassInfo source, Class<?> classType, Type genericType, Map<Class<Annotation>, Object> options, AnnotatedType annotatedType) {
		if (options.containsKey(SerComplexSubClass.class) || options.containsKey(SerComplexSubClasses.class)) {
			throw ThrowHandler.fatal(
					IllegalStateException::new, "NYI: handling of SerComplexSubClass annotation",
					ThrowHandler.ThrowEntry.of("Source", source),
					ThrowHandler.ThrowEntry.of("ClassType", classType),
					ThrowHandler.ThrowEntry.of("Annotations", options));
		}

		Class<?>[] subclasses = (Class<?>[]) options.get(SerSubclasses.class);
		List<TypeInfo> subInfos = new ArrayList<>(subclasses.length);

		for (Class<?> subclass : subclasses) {
			var subClassInfo = this.createClassInfoFromPolymorphicType(source, classType, genericType, annotatedType, subclass);
			subInfos.add(subClassInfo);
		}

		return new PolymorphicTypeInfo(classType, options, subInfos);
	}

	private void checkConstructor(List<FieldMetadata> fields, ClassInfo source) {
		try {
			Constructor<?> constructor = source.clazz.getDeclaredConstructor(fields.stream().map(fieldInfo -> fieldInfo.clazz.getRawClass()).toArray(Class[]::new));
			ThrowHandler.checkAccess(constructor.getModifiers(), () -> ThrowHandler.constructorAccessFail(constructor, source));
		} catch (NoSuchMethodException e) {
			throw ThrowHandler.constructorNotFoundFail(fields, source);
		}
	}


	public TypeInfo createClassInfo(ClassInfo source, Class<?> classType, Type genericType, @Nullable AnnotatedType annotatedType) {
		if (source == null) {
			throw ThrowHandler.fatal(NullPointerException::new, "source is null",
					ThrowHandler.ThrowEntry.of("ClassType", classType),
					ThrowHandler.ThrowEntry.of("Type", genericType),
					ThrowHandler.ThrowEntry.of("AnnotatedType", annotatedType)
			);
		}

		var options = AnnotationParser.parseAnnotations(annotatedType, hyphenAnnotations);

		// check if field is polymorphic
		if (options.containsKey(SerSubclasses.class) || options.containsKey(SerComplexSubClass.class) || options.containsKey(SerComplexSubClasses.class)) {
			return this.createPolymorphicClass(source, classType, genericType, options, annotatedType);
		}


		//Object / int / Object[] / int[]
		if (genericType instanceof Class clazz) {
			return new ClassInfo(clazz, options, this);
		}


		//Thing<T,T>
		if (genericType instanceof ParameterizedType type) {
			if (annotatedType instanceof AnnotatedParameterizedType parameterizedType) {
				LinkedHashMap<String, TypeInfo> out = mapTypes(source, type, parameterizedType);
				return new ParameterizedClassInfo((Class<?>) type.getRawType(), options, this, out);
			}
			throw new RuntimeException();
		}

		//T thing
		if (genericType instanceof TypeVariable typeVariable) {
			if (source instanceof ParameterizedClassInfo info) {
				String typeName = typeVariable.getName();
				TypeInfo classInfo = info.types.get(typeName);
				if (classInfo != null) {
					// safety first!
					// kropp: why are we copying?
					return new TypeClassInfo(classInfo.clazz, classInfo.annotations, typeName, castType(typeVariable.getBounds()[0]), classInfo);
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
				return new ArrayInfo(classType, options, classInfo);
			}
			throw new RuntimeException();
		}

		return null;
	}


	protected TypeInfo createClassInfoFromPolymorphicType(ClassInfo source, Class<?> poly, Type genericType, AnnotatedType annotatedGenericType, Class<?> subType) {
		TypeVariable<? extends Class<?>>[] typeParameters = subType.getTypeParameters();

		if (typeParameters.length != 0) {
			// let's try to figure out the types
			if (genericType instanceof ParameterizedType parameterizedType) {
				Map<String, AnnotatedType> types = this.findTypes(subType, poly, parameterizedType, (AnnotatedParameterizedType) annotatedGenericType);

				if (types == null) {
					throw ThrowHandler.fatal(
							ClassScanException::new,
							"Failed to find the type",
							ThrowHandler.ThrowEntry.of("SourceClass", source),
							ThrowHandler.ThrowEntry.of("SubType", subType),
							ThrowHandler.ThrowEntry.of("Poly", poly),
							ThrowHandler.ThrowEntry.of("Type", parameterizedType)
					);
				}

				LinkedHashMap<String, TypeInfo> typeInfoMap = mapAllTypes(source, typeParameters, types);

				return new ParameterizedClassInfo(subType, Map.of(), this, typeInfoMap);
			} else {
				// TODO: error
				throw new IllegalArgumentException("UWU");
			}
		}

		//Object / int / Object[] / int[]
		if (true /* genericType instanceof Class clazz */) {
			return new ClassInfo(subType, Map.of(), this);
		}


		//Thing<T,T>
		if (genericType instanceof ParameterizedType type) {
			// TODO: think
			/*
			if (annotatedType instanceof AnnotatedParameterizedType parameterizedType) {
				LinkedHashMap<String, TypeInfo> out = mapTypes(source, type, parameterizedType);
				return new ParameterizedClassInfo((Class<?>) type.getRawType(), options, this, out);
			}*/
			throw new RuntimeException();
		}

		//T thing
		if (genericType instanceof TypeVariable typeVariable) {
			LinkedHashMap<String, TypeInfo> typeMap;
			if (source instanceof ParameterizedClassInfo info) {
				typeMap = info.types;
			} else typeMap = new LinkedHashMap<>();
			var classInfo = typeMap.get(typeVariable.getName());

			if (classInfo == null) {
				throw ThrowHandler.typeFail("Type could not be identified", source, poly, typeVariable);
			}
			//safety first!
			return classInfo.copy();
		}

		//T[] arrrrrrrr
		if (genericType instanceof GenericArrayType genericArrayType) {
			//get component class
			// TODO: think
			/*
			if (annotatedType instanceof AnnotatedArrayType annotatedArrayType) {
				var componentType = genericArrayType.getGenericComponentType();
				var classInfo = createClassInfo(source, classType, componentType, annotatedArrayType.getAnnotatedGenericComponentType());
				if (classInfo == null) {
					throw ThrowHandler.typeFail("Array component could not be identified", source, classType, componentType);
				}
				return new ArrayInfo(classType, options, classInfo);
			}*/
			throw new RuntimeException();
		}

		return null;
	}

	@NotNull
	private LinkedHashMap<String, TypeInfo> mapAllTypes(
			ClassInfo source,
			TypeVariable<? extends Class<?>>[] typeParameters,
			Map<String, ? extends AnnotatedType> types) {

		LinkedHashMap<String, TypeInfo> typeInfoMap = new LinkedHashMap<>(typeParameters.length);

		for (TypeVariable<? extends Class<?>> typeParameter : typeParameters) {
			AnnotatedType annotatedType = types.get(typeParameter.getName());

			if (annotatedType == null || annotatedType == UNKNOWN.UNKNOWN) {
				throw ThrowHandler.fatal(IllegalStateException::new, "Did not find type",
						ThrowHandler.ThrowEntry.of("TypeName", typeParameter.getName())
				);
			}

			typeInfoMap.put(typeParameter.getName(), this.mapType(source, annotatedType));
		}

		return typeInfoMap;
	}

	private Map<String, AnnotatedType> findTypes(Class<?> subType, Class<?> poly, ParameterizedType genericPoly, AnnotatedParameterizedType annotatedGenericPoly) {
		if (subType == null) {
			return null;
		} else if (subType == poly) {
			TypeVariable<? extends Class<?>>[] typeParameters = subType.getTypeParameters();
			AnnotatedType[] actualTypeArguments = annotatedGenericPoly.getAnnotatedActualTypeArguments();

			assert typeParameters.length == actualTypeArguments.length;

			Map<String, AnnotatedType> types = new HashMap<>(typeParameters.length);

			for (int i = 0; i < typeParameters.length; i++) {
				types.put(typeParameters[i].getName(), actualTypeArguments[i]);
			}

			return types;
		} else {
			Type superclass = subType.getGenericSuperclass();
			var types = this.findTypes(subType.getSuperclass(), poly, genericPoly, annotatedGenericPoly);
			if (types == null) {
				Type[] genericInterfaces = subType.getGenericInterfaces();
				Class<?>[] interfaces = subType.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					types = this.findTypes(interfaces[i], poly, genericPoly, annotatedGenericPoly);
					if (types != null) {
						superclass = genericInterfaces[i];
						break;
					}
				}
			}

			if (types == null) {
				return null;
			}


			if (superclass instanceof ParameterizedType parameterizedType
			) {
				Type[] superTypeArguments = parameterizedType.getActualTypeArguments();
				TypeVariable<?>[] typeParameters = ((Class<?>) parameterizedType.getRawType()).getTypeParameters();

				assert superTypeArguments.length == typeParameters.length;

				var map = new HashMap<String, AnnotatedType>();

				for (int i = 0; i < superTypeArguments.length; i++) {
					this.resolveType(types, map, superTypeArguments[i], types.get(typeParameters[i].getName()));
				}

				for (TypeVariable<? extends Class<?>> typeParameter : subType.getTypeParameters()) {
					if (!map.containsKey(typeParameter.getName())) {
						map.put(typeParameter.getName(), UNKNOWN.UNKNOWN);
					}
				}


				return map;
			}
			throw new IllegalStateException("Not yet Implemented");

		}
	}

	static class UNKNOWN implements AnnotatedType, Type {
		static final UNKNOWN UNKNOWN = new UNKNOWN();

		private UNKNOWN() {
		}

		@Override
		public Type getType() {
			return this;
		}

		@Override
		public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
			return null;
		}

		@Override
		public Annotation[] getAnnotations() {
			return new Annotation[0];
		}

		@Override
		public Annotation[] getDeclaredAnnotations() {
			return new Annotation[0];
		}
	}

	private void resolveType(
			Map<String, AnnotatedType> lookup,
			Map<String, AnnotatedType> resolved,
			Type superTypeArgument,
			AnnotatedType type) {

		if (superTypeArgument instanceof Class<?> clazz) {

			if (type.getType() == clazz) {
				// all is fine
			} else if (type.getType() == UNKNOWN.UNKNOWN) {
				// resolve type that was unknown
				// I don't think we have to do something here? Although I do think there might be invalid case that we
				// need to consider
			} else {
				// TODO: handle `? extends` and `? super`
				throw new IllegalArgumentException("Could not unify types, " + type + " with " + type.getType() + " and " + clazz.getName());
			}
		} else if (superTypeArgument instanceof TypeVariable<?> typeVariable) {
			if (resolved.containsKey(typeVariable.getName())) {
				// check if it's the same
				if (resolved.get(typeVariable.getName()).equals(type)) {
					// all is fine
				} else {
					throw ThrowHandler.fatal(IllegalArgumentException::new, "Invalid type unification",
							ThrowHandler.ThrowEntry.of("Lookup", lookup),
							ThrowHandler.ThrowEntry.of("Resolved", resolved),
							ThrowHandler.ThrowEntry.of("SuperType", superTypeArgument),
							ThrowHandler.ThrowEntry.of("Type", type),
							ThrowHandler.ThrowEntry.of("Previously discovered type", resolved.get(typeVariable.getName()))
					);
				}
			} else {
				// TODO: check bounds?
				resolved.put(typeVariable.getName(), type);

				Type[] bounds = typeVariable.getBounds();

				for (Type bound : bounds) {
					if (bound == Object.class) {
						// TODO: i think this shouldn't be a special case here and instead be handled by class/class unifying?
						continue;
					}

					// eg Foo<A, B extends List<A>> extends Bar<B> with Bar<List<Int>>
					this.resolveType(lookup, resolved, bound, type);
				}

			}
		} else if (superTypeArgument instanceof ParameterizedType superParameterizedType &&
				type.getType() instanceof ParameterizedType selfParameterizedType &&
				type instanceof AnnotatedParameterizedType selfAnnotatedParameterizedType
		) {
			if (superParameterizedType.getRawType().equals(selfParameterizedType.getRawType())) {
				Type[] superTypeArguments = superParameterizedType.getActualTypeArguments();
				AnnotatedType[] selfTypeArguments = selfAnnotatedParameterizedType.getAnnotatedActualTypeArguments();

				assert superTypeArguments.length == selfTypeArguments.length;

				for (int i = 0; i < superTypeArguments.length; i++) {
					AnnotatedType selfType = selfTypeArguments[i];
					Type superType = superTypeArguments[i];

					this.resolveType(lookup, resolved, superType, selfType);
				}
			} else {
				throw ThrowHandler.fatal(IllegalStateException::new, "NYI: parameterized type unification through supertypes",
						ThrowHandler.ThrowEntry.of("Lookup", lookup),
						ThrowHandler.ThrowEntry.of("Resolved", resolved),
						ThrowHandler.ThrowEntry.of("SuperType", superTypeArgument),
						ThrowHandler.ThrowEntry.of("Type", type),
						ThrowHandler.ThrowEntry.of("TypeType", type.getType())
				);
			}
		} else {
			throw ThrowHandler.fatal(IllegalArgumentException::new, "Unexpected type unification request",
					ThrowHandler.ThrowEntry.of("Lookup", lookup),
					ThrowHandler.ThrowEntry.of("Resolved", resolved),
					ThrowHandler.ThrowEntry.of("SuperType", superTypeArgument),
					ThrowHandler.ThrowEntry.of("Type", type)
			);
		}
	}


	//map all of the types,  A<String,Integer> -> B<K,S> == B<K = String, S = Integer>
	private LinkedHashMap<String, TypeInfo> mapTypes(ClassInfo source, ParameterizedType type, AnnotatedParameterizedType annotatedType) {
		var out = new LinkedHashMap<String, TypeInfo>();
		var clazz = (Class<?>) type.getRawType();
		var innerTypes = clazz.getTypeParameters();
		var annotatedParameters = annotatedType.getAnnotatedActualTypeArguments();
		var parameters = type.getActualTypeArguments();
		for (int i = 0; i < parameters.length; i++) {
			out.put(innerTypes[i].getName(), this.createClassInfo(source, clazz, parameters[i], annotatedParameters[i]));
		}
		return out;
	}

	private TypeInfo mapType(ClassInfo source, AnnotatedType annotatedType) {
		return this.createClassInfo(source, castType(annotatedType.getType()), annotatedType.getType(), annotatedType);
	}

	private static Class<?> castType(Type type) {
		if (type instanceof Class<?> c) {
			return c;
		} else if (type instanceof ParameterizedType parameterizedType) {
			return castType(parameterizedType.getRawType());
		} else {
			throw new IllegalStateException("Blame alpha: " + type.getClass().getSimpleName() + ": " + type.getTypeName());
		}
	}
}
