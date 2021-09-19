package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.annotation.SerComplexSubClass;
import dev.quantumfusion.hyphen.annotation.SerComplexSubClasses;
import dev.quantumfusion.hyphen.annotation.SerNull;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.data.info.TypeInfo;
import dev.quantumfusion.hyphen.data.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.gen.impl.AbstractDef;
import dev.quantumfusion.hyphen.gen.impl.IntDef;
import dev.quantumfusion.hyphen.options.*;
import dev.quantumfusion.hyphen.thr.IllegalClassException;
import dev.quantumfusion.hyphen.thr.ThrowHandler;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SerializerFactory {
	public final Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> implementations = new HashMap<>();
	public final Map<Class<? extends Annotation>, OptionParser<?>> hyphenAnnotations = new AnnotationParser.AnnotationOptionMap<>();
	public final Map<TypeInfo, SerializerMetadata> methods = new HashMap<>();
	private final boolean debug;
	private final Class<?> clazz;

	protected SerializerFactory(boolean debug, Class<?> clazz) {
		this.debug = debug;
		this.clazz = clazz;
	}


	public static SerializerFactory create(Class<?> clazz) {
		return createInternal(false, clazz);
	}

	public static SerializerFactory createDebug(Class<?> clazz) {
		return createInternal(true, clazz);
	}

	private static SerializerFactory createInternal(boolean debugMode, Class<?> clazz) {
		final SerializerFactory scanHandler = new SerializerFactory(debugMode, clazz);
		scanHandler.addImpl(int.class, (field) -> new IntDef());
		scanHandler.addTestImpl(Integer.class, Float.class, List.class);
		scanHandler.addOption(SerNull.class, new ExistsOption());
		scanHandler.addOption(SerSubclasses.class, new ArrayOption<>(SerSubclasses::value));
		scanHandler.addOption(SerComplexSubClass.class, new SimpleAnnotationOption<>());
		scanHandler.addOption(SerComplexSubClasses.class, new ArrayOption<>(SerComplexSubClasses::value));
		return scanHandler;
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

	public void build() {
		if (clazz.getTypeParameters().length > 0) {
			throw ThrowHandler.fatal(IllegalClassException::new, "The Input class has Parameters,");
		}
		ScanHandler scanner = new ScanHandler(methods, implementations, hyphenAnnotations, debug);
		scanner.scan(clazz);
	}
}
