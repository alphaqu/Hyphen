package net.oskarstrom.hyphen.data;

import java.lang.reflect.Field;

public class FieldMetadata {
	public final Field field;
	public final boolean superField;


	public FieldMetadata(Field field, boolean superField) {
		this.field = field;
		this.superField = superField;
	}



}
