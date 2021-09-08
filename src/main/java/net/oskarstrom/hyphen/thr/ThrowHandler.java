package net.oskarstrom.hyphen.thr;

import net.oskarstrom.hyphen.data.ClassInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.oskarstrom.hyphen.thr.ThrowHandler.ThrowEntry.of;

public class ThrowHandler {

	public static void checkAccess(int modifier, Supplier<RuntimeException> runnable) {
		if (Modifier.isProtected(modifier) || Modifier.isPrivate(modifier) || !Modifier.isPublic(modifier)) {
			throw runnable.get();
		}
	}
	// some methods to shorten code
	public static RuntimeException typeFail(String reason, ClassInfo info, Class<?> clazz, Type type) {
		return fatal(ClassScanException::new, reason, new ThrowEntry[]{
				of("Source Class", info.clazz.getName()),
				of("Error Class", clazz.getName()),
				of("Type Name", type.getTypeName()),
				of("Type Class", type.getClass().getSimpleName())
		});
	}

	public static RuntimeException fieldAccessFail(Field field, ClassInfo source) {
		return fatal(AccessException::new, "Field is inaccessible as it's " + getModifierName(field.getModifiers()), new ThrowEntry[]{
				of("Field Name", field.getName()),
				of("Field Class", field.getType().getSimpleName()),
				of("Source Class", source.clazz.getName())
		});
	}

	public static RuntimeException constructorAccessFail(Constructor<?> constructor, ClassInfo source) {
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



	public static RuntimeException constructorNotFoundFail(List<Field> fields, ClassInfo info) {
		ThrowHandler.Throwable[] throwable = new ThrowHandler.Throwable[2 + fields.size()];
		throwable[0] = ThrowHandler.ThrowEntry.of("Source Class", info.clazz.getSimpleName());
		throwable[1] = ThrowHandler.ThrowEntry.of("Expected Constructor Parameters", "");
		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			throwable[i + 2] = ThrowHandler.ThrowEntry.of("\t" + field.getName(), field.getType().getSimpleName());
		}
		throw ThrowHandler.fatal(AccessException::new, "Matching Constructor does not exist", throwable);
	}


	//dollar for the styles.
	//returns just for javac to stfu in some spots
	public static RuntimeException fatal(Function<String, RuntimeException> ex, String reason, Throwable... throwable) {
		StringBuilder builder = new StringBuilder();
		builder.append('\n');
		builder.append('\n');
		builder.append("Reason: ");
		builder.append("\n\t");
		builder.append(reason);
		builder.append('\n');
		builder.append('\n');
		builder.append("Detail: ");
		for (Throwable throwable$ : throwable) {
			builder.append('\n');
			for (ThrowEntry entry : throwable$.getEntries()) {
				builder.append(entry);
			}
		}
		builder.append('\n');
		builder.append('\n');
		builder.append("Stacktrace: ");
		throw ex.apply(builder.toString());
	}

	public interface Throwable {
		ThrowEntry[] getEntries();
	}

	public static class ThrowEntry implements Throwable {
		public final String key;
		public final String value;

		public ThrowEntry(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public static ThrowEntry of(String key, String value) {
			return new ThrowEntry(key, value);
		}

		@Override
		public ThrowEntry[] getEntries() {
			return new ThrowEntry[]{this};
		}

		@Override
		public String toString() {
			return "\t" + key + ": " + value;
		}
	}

}
