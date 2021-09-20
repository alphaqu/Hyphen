package dev.quantumfusion.hyphen.thr;

import dev.quantumfusion.hyphen.data.FieldEntry;
import dev.quantumfusion.hyphen.data.info.ClassInfo;
import dev.quantumfusion.hyphen.data.info.TypeInfo;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static dev.quantumfusion.hyphen.thr.ThrowEntry.of;

public class ThrowHandler {

	public static void checkAccess(int modifier, Supplier<? extends RuntimeException> runnable) {
		if (Modifier.isProtected(modifier) || Modifier.isPrivate(modifier) || !Modifier.isPublic(modifier)) {
			throw runnable.get();
		}
	}

	// some methods to shorten code
	public static RuntimeException typeFail(String reason, TypeInfo source, Class<?> clazz, Type type) {
		return fatal(ClassScanException::new, reason, new ThrowEntry[]{
				of("Source Class", source.clazz.getName()),
				of("Error Class", clazz.getName()),
				of("Type Name", type.getTypeName()),
				of("Type Class", type.getClass().getSimpleName())
		});
	}

	public static RuntimeException typeFail(String reason, TypeInfo source, Field field) {
		return fatal(ClassScanException::new, reason, new ThrowEntry[]{
				of("Source Class", source.clazz.getName()),
				of("Field Class", field.getType()),
				of("Field Name", field.getName()),
				of("Type", field.getGenericType())
		});
	}

	public static RuntimeException fieldAccessFail(FieldEntry field, TypeInfo source) {
		return fatal(AccessException::new, "Field is inaccessible as it's " + getModifierName(field.modifier), new ThrowEntry[]{
				of("Field Name", field.name),
				of("Field Class", field.clazz.clazz.getSimpleName()),
				of("Source Class", source.clazz.getName())
		});
	}

	public static RuntimeException constructorAccessFail(Constructor<?> constructor, TypeInfo source) {
		return fatal(AccessException::new, "Constructor is inaccessible as it's " + getModifierName(constructor.getModifiers()), new ThrowEntry[]{
				of("Class", source.clazz.getName())
		});
	}

	private static String getModifierName(int modifier) {
		String access;
		if (Modifier.isPrivate(modifier)) {
			access = "\"private\"";
		} else if (Modifier.isProtected(modifier)) {
			access = "\"protected\"";
		} else {
			access = "\"package-private\"";
		}
		return access;
	}


	public static RuntimeException constructorNotFoundFail(List<FieldEntry> fields, ClassInfo info) {
		ThrowHandler.Throwable[] throwable = new ThrowHandler.Throwable[2 + fields.size()];
		throwable[0] = ThrowEntry.of("Source Class", info.clazz.getSimpleName());
		throwable[1] = ThrowEntry.of("Expected Constructor Parameters", "");
		for (int i = 0; i < fields.size(); i++) {
			FieldEntry fieldInfo = fields.get(i);
			throwable[i + 2] = ThrowEntry.of('\t' + fieldInfo.name, fieldInfo.clazz.getRawClass().getSimpleName());
		}
		throw ThrowHandler.fatal(AccessException::new, "Matching Constructor does not exist", throwable);
	}


	//returns just for javac to stfu in some spots
	@Contract("_, _, _ -> fail")
	public static HypenException fatal(Function<? super String, ? extends RuntimeException> ex, String reason, Throwable... throwable) {
		RuntimeException runtimeException = ex.apply(reason);
		if (runtimeException instanceof HypenException hypenException)
			throw hypenException.addEntries(throwable);
		else
			throw new HypenException(runtimeException).addEntries(throwable);
	}


	public interface Throwable {
		ThrowEntry[] getEntries();
	}

}