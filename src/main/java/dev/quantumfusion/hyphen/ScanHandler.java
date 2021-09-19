package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.data.FieldEntry;
import dev.quantumfusion.hyphen.data.info.ClassInfo;
import dev.quantumfusion.hyphen.data.info.PolymorphicTypeInfo;
import dev.quantumfusion.hyphen.data.info.TypeInfo;
import dev.quantumfusion.hyphen.data.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.gen.impl.MethodCallDef;
import dev.quantumfusion.hyphen.options.OptionParser;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ScanHandler {
	public static final TypeInfo UNKNOWN_INFO = ClassInfo.create(null, null);
	public final Map<TypeInfo, SerializerMetadata> methods;
	public final Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> implementations;
	public final Map<Class<? extends Annotation>, OptionParser<?>> hyphenAnnotations;
	@Nullable
	private final DebugHandler debugHandler;
<<<<<<< HEAD:src/main/java/dev/quantumfusion/hyphen/ScanHandler.java
=======
	public static final TypeInfo UNKNOWN_INFO = new ClassInfo(null, null){
		@Override
		public String toString() {
			return "UNKNOWN";
		}
	};
>>>>>>> origin/union-types:src/main/java/net/oskarstrom/hyphen/ScanHandler.java

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
