package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.annotation.HyphenAnnotation;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.info.TypeInfo;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ScanUtils {

	@Nullable
	public static Class<?>[] pathTo(Class<?> clazz, Predicate<? super Class<?>> matcher, Function<? super Class<?>, Class<?>[]> splitter, int depth) {
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

	public static Map<Class<? extends Annotation>, Annotation> getAnnotations(TypeInfo source, @Nullable AnnotatedType type) {
		var options = parseAnnotations(type == null ? null : type.getDeclaredAnnotations());
		options.putAll(source.classAnnotations);

		// find @Serialize on packages
		String packageName = source.getClazz().getPackageName();
		int i = packageName.length();
		do {
			Package p = ClassLoader
					.getSystemClassLoader()
					.getDefinedPackage(packageName.substring(0, i));
			if (p != null && p.isAnnotationPresent(Serialize.class)) {
				options.put(Serialize.class, p.getAnnotation(Serialize.class));
				break;
			}
		} while ((i = packageName.lastIndexOf('.', i - 1)) >= 0);
		return options;
	}

	public static Map<Class<? extends Annotation>, Annotation> parseAnnotations(Annotation @Nullable [] annotations) {
		var options = new HashMap<Class<? extends Annotation>, Annotation>();
		if (annotations != null) {
			for (Annotation declaredAnnotation : annotations) {
				if (declaredAnnotation.annotationType().getDeclaredAnnotation(HyphenAnnotation.class) != null) {
					options.put(declaredAnnotation.annotationType(), declaredAnnotation);
				}
			}
		}
		return options;
	}

	public static void checkAccess(int modifier, Supplier<? extends RuntimeException> runnable) {
		if (Modifier.isProtected(modifier) || Modifier.isPrivate(modifier) || !Modifier.isPublic(modifier)) {
			throw runnable.get();
		}
	}
}