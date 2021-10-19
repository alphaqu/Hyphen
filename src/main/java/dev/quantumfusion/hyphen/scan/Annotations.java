package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.scan.annotations.Data;
import dev.quantumfusion.hyphen.scan.annotations.InheritableAnnotation;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import org.jetbrains.annotations.Contract;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Annotations {
	private final Class<?> containingClass;
	private final Map<Class<? extends Annotation>, Annotation> thisClass;

	public Annotations(Class<?> containingClass, Map<Class<? extends Annotation>, Annotation> thisClass) {
		this.containingClass = containingClass;
		this.thisClass = thisClass;
	}


	private static final Annotations EMPTY = new Annotations(Object.class, Map.of());

	public static Annotations empty() {
		return EMPTY;
	}

	public static Annotations of(AnnotatedType type, Clazz ctx) {
		var map = new LinkedHashMap<Class<? extends Annotation>, Annotation>();

		for (Annotation annotation : type.getAnnotations()) {
			map.put(annotation.annotationType(), annotation);
		}

		return new Annotations(ctx.getDefinedClass(), map);
	}

	@SuppressWarnings("unchecked")
	@Contract(pure = true)
	public <A extends Annotation> A get(Class<A> annotation) {
		Annotation ann = this.thisClass.get(annotation);
		if (ann != null) return (A) ann;
		if (annotation.isAnnotationPresent(InheritableAnnotation.class)) {
			if (this.containingClass.isAnnotationPresent(annotation))
				return this.containingClass.getDeclaredAnnotation(annotation);
			Package pack = this.containingClass.getPackage();
			if (pack != null && pack.isAnnotationPresent(annotation))
				return pack.getDeclaredAnnotation(annotation);
		}
		return null;
	}

	public boolean containsKey(Class<? extends Annotation> annotation) {
		return this.thisClass.containsKey(annotation) ||
				annotation.isAnnotationPresent(InheritableAnnotation.class) &&
						(this.containingClass.isAnnotationPresent(annotation) ||
								(this.containingClass.getPackage() != null &&
										this.containingClass.getPackage().isAnnotationPresent(annotation)));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		Annotations that = (Annotations) o;
		return Objects.equals(this.containingClass, that.containingClass) && Objects.equals(this.thisClass, that.thisClass);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.containingClass, this.thisClass);
	}

	@SuppressWarnings("unchecked")
	static private final Class<? extends Annotation>[] INCLUDE = new Class[]{Data.class};

	@Override
	public String toString() {
		var sb = new StringBuilder();

		for (var value : this.thisClass.values()) {
			sb.append(getString(value)).append(" ");
		}

		for (var annClass : INCLUDE) {
			Annotation value;
			if (!this.thisClass.containsKey(annClass) && (value = this.get(annClass)) != null)
				// external annotation
				sb.append(getString(value)).append(" ");
		}

		return sb.toString();
	}

	private static String getString(Annotation value) {
		try {
			String simpleName = "@" + value.annotationType().getSimpleName();
			StringJoiner inner = new StringJoiner(", ", simpleName + "(", ")");
			inner.setEmptyValue(simpleName);

			Method[] methods = value.annotationType().getMethods();

			for (Method method : methods) {
				if (method.getDeclaringClass() == Annotation.class) continue;
				Object def = method.getDefaultValue();

				Object invoke = method.invoke(value);

				if (!Objects.deepEquals(def, invoke)) {
					if (invoke instanceof Object[] objects)
						inner.add(method.getName() + " = " + Arrays.deepToString(objects));
					else
						inner.add(method.getName() + " = " + invoke);
				}
			}

			return inner.toString();
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
