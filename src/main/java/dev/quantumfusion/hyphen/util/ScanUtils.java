package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.annotation.HyphenOptionAnnotation;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.gen.metadata.ClassSerializerMetadata;
import dev.quantumfusion.hyphen.info.ClassInfo;
import dev.quantumfusion.hyphen.info.ParameterizedInfo;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ScanUtils {

	public static Class<?>[] pathTo(Class<?> clazz, Predicate<? super Class<?>> matcher, Function<Class<?>, Class<?>[]> splitter, int depth) {
		if (matcher.test(clazz))
			return new Class[depth];

		for (Class<?> aClass : splitter.apply(clazz)) {
			Class<?>[] classes = pathTo(aClass, matcher, splitter, depth + 1);
			if (classes != null) {
				classes[depth] = aClass;
				return classes;
			}
		}

		return null;
	}

	public static Type[] pathTo(Class<?> clazz, Class<?> targetParent, int depth) {
		if (clazz == targetParent)
			return new Type[depth];

		for (Type aClass : TypeUtil.getInheritedTypes(clazz)) {
			Type[] classes = pathTo(getClazz(aClass), targetParent, depth + 1);
			if (classes != null) {
				classes[depth] = aClass;
				return classes;
			}
		}

		return null;
	}

	public static Class<?> getClazz(Type type) {
		if (type instanceof Class<?> c) return c;
		else if (type instanceof ParameterizedType parameterizedType) return getClazz(parameterizedType.getRawType());
		else
			throw new IllegalArgumentException("Blame kroppeb: " + type.getClass().getSimpleName() + ": " + type.getTypeName());
	}

	public static Class<?> getClazzOrNull(Type type) {
		if (type instanceof Class<?> c) return c;
		else if (type instanceof ParameterizedType parameterizedType) return getClazz(parameterizedType.getRawType());
		else return null;
	}

	public static Map<Class<? extends Annotation>, Annotation> parseAnnotations(@Nullable AnnotatedType type) {
		var options = new HashMap<Class<? extends Annotation>, Annotation>();
		if (type != null) {
			for (Annotation declaredAnnotation : type.getDeclaredAnnotations()) {
				if (declaredAnnotation.annotationType().getDeclaredAnnotation(HyphenOptionAnnotation.class) != null) {
					options.put(declaredAnnotation.annotationType(), declaredAnnotation);
				}
			}
		}
		return options;
	}

	public static void checkConstructor(ScanHandler factory, ClassInfo source) {
		ClassInfo parent = source;
		if (source instanceof ParameterizedInfo classInfo) {
			parent = classInfo.copyWithoutTypeKnowledge();
		}
		List<ClassSerializerMetadata.FieldEntry> allFields = parent.getAllFields(factory, field -> field.getDeclaredAnnotation(Serialize.class) != null);
		Class<?>[] classes = new Class[allFields.size()];
		for (int i = 0; i < allFields.size(); i++) {
			classes[i] = allFields.get(i).clazz().getClazz();
		}
		try {
			Constructor<?> constructor = source.clazz.getDeclaredConstructor(classes);
			checkAccess(constructor.getModifiers(), () -> ThrowHandler.constructorAccessFail(constructor, source));
		} catch (NoSuchMethodException e) {
			throw ThrowHandler.constructorNotFoundFail(allFields, source);
		}
	}

	public static void checkAccess(int modifier, Supplier<? extends RuntimeException> runnable) {
		if (Modifier.isProtected(modifier) || Modifier.isPrivate(modifier) || !Modifier.isPublic(modifier)) {
			throw runnable.get();
		}
	}
}