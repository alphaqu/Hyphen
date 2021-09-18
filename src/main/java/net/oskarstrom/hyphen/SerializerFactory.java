package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.annotation.SerComplexSubClass;
import net.oskarstrom.hyphen.annotation.SerComplexSubClasses;
import net.oskarstrom.hyphen.annotation.SerNull;
import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.data.FieldEntry;
import net.oskarstrom.hyphen.data.info.ClassInfo;
import net.oskarstrom.hyphen.data.info.PolymorphicTypeInfo;
import net.oskarstrom.hyphen.data.info.TypeInfo;
import net.oskarstrom.hyphen.data.metadata.SerializerMetadata;
import net.oskarstrom.hyphen.gen.impl.AbstractDef;
import net.oskarstrom.hyphen.gen.impl.IntDef;
import net.oskarstrom.hyphen.gen.impl.MethodCallDef;
import net.oskarstrom.hyphen.options.*;
import net.oskarstrom.hyphen.thr.IllegalClassException;
import net.oskarstrom.hyphen.thr.ThrowHandler;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

public class SerializerFactory {
	public final Map<TypeInfo, SerializerMetadata> methods = new HashMap<>();
	public final Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> implementations = new HashMap<>();
	public final Map<Class<? extends Annotation>, OptionParser<?>> hyphenAnnotations = new AnnotationParser.AnnotationOptionMap<>();
	@Nullable
	private final DebugHandler debugHandler;


	protected SerializerFactory(boolean debug) {
		if (debug) this.debugHandler = new DebugHandler(this);
		else this.debugHandler = null;
	}


	public static SerializerFactory create() {
		return createInternal(false);
	}

	public static SerializerFactory createDebug() {
		return createInternal(true);
	}

	private static SerializerFactory createInternal(boolean debugMode) {
		final SerializerFactory serializerFactory = new SerializerFactory(debugMode);
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

		createSerializeMetadataInternal(new ClassInfo(clazz, new HashMap<>()));

		if (debugHandler != null) {
			debugHandler.printMethods(methods);
		}
	}

	private SerializerMetadata createSerializeMetadataInternal(TypeInfo typeInfo) {
		return typeInfo.createMeta(this);
	}

	public SerializerMetadata createSerializeMetadata(TypeInfo typeInfo) {
		if (this.methods.containsKey(typeInfo)) {
			return this.methods.get(typeInfo);
		}

		SerializerMetadata serializerMetadata = createSerializeMetadataInternal(typeInfo);
		this.methods.put(typeInfo, serializerMetadata);
		return serializerMetadata;
	}

	public ObjectSerializationDef getDefinition(FieldEntry field, ClassInfo source) {
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

	public void checkConstructor(List<FieldEntry> fields, ClassInfo source) {
		try {
			Constructor<?> constructor = source.clazz.getDeclaredConstructor(fields.stream().map(fieldInfo -> fieldInfo.clazz.getRawClass()).toArray(Class[]::new));
			ThrowHandler.checkAccess(constructor.getModifiers(), () -> ThrowHandler.constructorAccessFail(constructor, source));
		} catch (NoSuchMethodException e) {
			throw ThrowHandler.constructorNotFoundFail(fields, source);
		}
	}

	public static class UNKNOWN implements AnnotatedType, Type {
		public static final UNKNOWN UNKNOWN = new UNKNOWN();

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


}
