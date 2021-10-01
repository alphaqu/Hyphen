package dev.quantumfusion.hyphen.util;

import dev.quantumfusion.hyphen.io.IOInterface;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class IOUtil {
	private static final Method[] IO_METHODS = IOInterface.class.getDeclaredMethods();

	public static sun.misc.Unsafe getUnsafeInstance() {
		Class<sun.misc.Unsafe> clazz = sun.misc.Unsafe.class;
		for (Field field : clazz.getDeclaredFields()) {
			if (!field.getType().equals(clazz))
				continue;
			final int modifiers = field.getModifiers();
			if (!(Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)))
				continue;
			try {
				field.setAccessible(true);
				return (sun.misc.Unsafe) field.get(null);
			} catch (Exception ignored) {
			}
			break;
		}

		throw new IllegalStateException("Unsafe is unavailable.");
	}

	public static void checkIOImpl(Class<?> clazz) throws NoSuchMethodException {
		for (Method ioMethod : IO_METHODS) {
			final Method declaredMethod = clazz.getDeclaredMethod(ioMethod.getName(), ioMethod.getParameterTypes());
			if (ioMethod.getReturnType() != declaredMethod.getReturnType())
				throw new NoSuchMethodException(ioMethod.getName());
		}
	}
}
