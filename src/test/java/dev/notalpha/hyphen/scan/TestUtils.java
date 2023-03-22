package dev.notalpha.hyphen.scan;

import dev.notalpha.hyphen.scan.data.Apple;
import dev.notalpha.hyphen.scan.data.Banana;
import dev.notalpha.hyphen.scan.data.Dog;
import dev.notalpha.hyphen.scan.data.Pet;

import java.lang.reflect.Field;

@Apple
@Banana
public class TestUtils {
	public static final Apple APPLE = TestUtils.class.getDeclaredAnnotation(Apple.class);
	public static final Banana BANANA = TestUtils.class.getDeclaredAnnotation(Banana.class);
	public static final Pet PET = Dog.class.getDeclaredAnnotation(Pet.class);

	public static Field getField(String name) {
		try {
			StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
			Class<?> aClass = Class.forName(stackTraceElement.getClassName());
			return aClass.getDeclaredField(name);
		} catch (ClassNotFoundException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}
