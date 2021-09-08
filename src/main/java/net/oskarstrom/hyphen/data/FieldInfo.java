package net.oskarstrom.hyphen.data;

import java.lang.reflect.Field;

public class FieldInfo {
	public final Field field;
	public final boolean superField;


	public FieldInfo(Field field, boolean superField) {
		this.field = field;
		this.superField = superField;
	}
}
