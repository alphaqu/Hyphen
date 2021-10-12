package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Direction;
import dev.quantumfusion.hyphen.FieldEntry;
import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.util.ScanUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

public class Clazz {
	public final Class<?> aClass;

	public Clazz(Class<?> aClass) {
		this.aClass = aClass;
	}

	public static Clazz create(Class<?> type, Clazz ctx, Direction dir) {
		if (type.isArray()) return dev.quantumfusion.hyphen.type.ArrayClazz.create(type, ctx, dir);
		return new Clazz(type);
	}


	public Clazz define(String typeName) {
		return ScanHandler.UNKNOWN;
	}

	public List<FieldEntry> asSub(Class<?> sub) {
		final Type[] path = ScanUtil.findPath(sub.getTypeParameters().length > 0 ? ScanUtil.wrap(sub) : sub, (test) -> ScanUtil.getClassFrom(test) == aClass, ScanUtil::getInherited);
		if (path == null)
			throw new RuntimeException(sub.getSimpleName() + " does not inherit " + aClass.getSimpleName());

		var fields = new LinkedHashMap<Field, Clazz>();
		var ctx = this;
		for (int i = path.length - 1; i >= 0; i--) {
			ctx = ScanHandler.create(path[i], ctx, Direction.SUB);
			for (var field : ctx.getFields()) {
				var f = field.field();
				var c = field.clazz();
				if (fields.containsKey(f) && (fields.get(f).defined() > c.defined())) continue;
				fields.put(f, c);
			}
		}

		var out = new ArrayList<FieldEntry>();
		fields.forEach((field, clazz) -> out.add(new FieldEntry(clazz, field)));
		return out;
	}

	public List<FieldEntry> getFields() {
		List<FieldEntry> fieldEntries = new ArrayList<>();
		if (aClass.getSuperclass() != null)
			fieldEntries.addAll(ScanHandler.create(aClass.getGenericSuperclass(), this, Direction.SUPER).getFields());

		for (Field field : aClass.getDeclaredFields())
			fieldEntries.add(new FieldEntry(ScanHandler.create(field.getGenericType(), this, Direction.NORMAL), field));

		return fieldEntries;
	}

	public int defined() {
		return 1;
	}

	@Override
	public String toString() {
		return aClass.getSimpleName();
	}
}
