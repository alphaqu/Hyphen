package net.oskarstrom.hyphen.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class FieldInfo extends ClassInfo {
	@NotNull
	public final ClassInfo source;
	@Nullable
	public final ClassInfo[] subclasses;
	final Field field;

	private FieldInfo(Class<?> clazz, TypeMap typeMap, @NotNull ClassInfo source, @Nullable ClassInfo[] subclasses, Field field) {
		super(clazz, typeMap, field.getGenericType());
		this.source = source;
		this.subclasses = subclasses;
		this.field = field;
	}

	public static FieldInfo create(@NotNull ClassInfo source, Field field, SubclassMap subclasses) {
		final ClassInfo fieldClass = source.getFieldClass(field);
		final Class<?> clazz = fieldClass.getClazz();
		return new FieldInfo(clazz, fieldClass.typeMap, source, subclasses.mapSubclasses(fieldClass), field);
	}

	public String getName() {
		return field.getName();
	}


}
