package net.oskarstrom.hyphen.data;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class FieldInfo extends ClassInfo {
	@NotNull
	public final ClassInfo source;
	final Field field;

	private FieldInfo(Class<?> clazz, TypeMap typeMap, @NotNull ClassInfo source, Field field) {
		super(clazz, typeMap);
		this.source = source;
		this.field = field;
	}

	public static FieldInfo create(@NotNull ClassInfo source, Field field) {
		final ClassInfo fieldClass = source.getFieldClass(field);
		return new FieldInfo(fieldClass.getClazz(), fieldClass.typeMap, source, field);
	}

	public String getName() {
		return field.getName();
	}




}
