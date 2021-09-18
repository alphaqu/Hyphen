package net.oskarstrom.hyphen;

import net.oskarstrom.hyphen.data.FieldEntry;
import net.oskarstrom.hyphen.data.info.ClassInfo;
import net.oskarstrom.hyphen.data.info.PolymorphicTypeInfo;
import net.oskarstrom.hyphen.data.info.TypeInfo;
import net.oskarstrom.hyphen.data.metadata.SerializerMetadata;
import net.oskarstrom.hyphen.gen.impl.MethodCallDef;
import net.oskarstrom.hyphen.options.OptionParser;
import net.oskarstrom.hyphen.thr.ThrowHandler;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ScanHandler {
	public final Map<TypeInfo, SerializerMetadata> methods;
	public final Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> implementations;
	public final Map<Class<? extends Annotation>, OptionParser<?>> hyphenAnnotations;
	@Nullable
	private final DebugHandler debugHandler;
	public static final TypeInfo UNKNOWN_INFO = ClassInfo.create(null, null);

	protected ScanHandler(Map<TypeInfo, SerializerMetadata> methods, Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> implementations, Map<Class<? extends Annotation>, OptionParser<?>> hyphenAnnotations, boolean debug) {
		this.implementations = implementations;
		this.hyphenAnnotations = hyphenAnnotations;
		this.debugHandler = debug ? new DebugHandler(this) : null;
		this.methods = methods;
	}

	public void scan(Class<?> clazz) {
		createSerializeMetadata(ClassInfo.create(clazz, new HashMap<>()));

		if (debugHandler != null) {
			debugHandler.printMethods(methods);
		}
	}

	private SerializerMetadata createSerializeMetadataInternal(TypeInfo typeInfo) {
		return typeInfo.createMetadata(this);
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
}
