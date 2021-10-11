package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.util.java.MapUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.HashMap;
import java.util.Map;

public record FieldType(Clz clazz,
						Map<Class<? extends Annotation>, ? extends Annotation> annotations,
						Map<Class<? extends Annotation>, ? extends Annotation> globalAnnotations) {

	public static FieldType of(Clz clz) {
		return new FieldType(clz, Map.of(), Map.of());
	}

	public FieldType resolve(Clazz context) {
		Clz resolved = this.clazz.resolve(context);
		if (resolved == this.clazz) return this;
		return new FieldType(resolved, this.annotations, this.globalAnnotations);
	}

	public FieldType map(FieldType otherFieldType, Map<TypeClazz, TypeClazz> types, MergeDirection mergeDirection) {
		// idk what to do with the annotations
		var merged = this.clazz.map(otherFieldType.clazz, types, mergeDirection);
		if (otherFieldType.clazz.equals(merged) && this.annotations.isEmpty() && this.globalAnnotations.isEmpty())
			return otherFieldType;

		// todo copy both annotations
		return new FieldType(merged,
				MapUtil.merge(HashMap::new, this.annotations, otherFieldType.annotations),
				MapUtil.merge(HashMap::new, this.globalAnnotations, otherFieldType.globalAnnotations));
	}

	public void finish(AnnotatedType type, Clazz source) {
		this.clazz.finish(type, source);
	}

	@Override
	public String toString() {
		return "FieldType{" +
				this.clazz +
				(this.annotations.isEmpty() ? "" : ", annotations=" + this.annotations) +
				(this.globalAnnotations.isEmpty() ? "" : ", globalAnnotations=" + this.globalAnnotations) +
				'}';
	}

	public Class<?> pullBytecodeClass() {
		return this.clazz.pullBytecodeClass();
	}

	public Class<?> pullClass() {
		return this.clazz.pullClass();
	}
}
