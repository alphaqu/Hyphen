package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.util.java.MapUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.HashMap;
import java.util.Map;

public record FieldType(Clz clazz,
						Map<Class<? extends Annotation>, ? extends Annotation> annotations,
						Map<Class<? extends Annotation>, ? extends Annotation> globalAnnotations) implements Clz{

	@Override
	public FieldType resolve(Clazz context) {
		Clz resolved = this.clazz.resolve(context);
		if (resolved == this.clazz) return this;
		return new FieldType(resolved, this.annotations, this.globalAnnotations);
	}

	@Override
	public FieldType map(Clz other, Map<TypeClazz, TypeClazz> types, MergeDirection mergeDirection) {
		if (other instanceof FieldType otherFieldType) {
			// idk what to do with the annotations
			var merged = this.clazz.map(otherFieldType.clazz, types, mergeDirection);
			if (otherFieldType.equals(merged) && this.annotations.isEmpty() && this.globalAnnotations.isEmpty())
				return otherFieldType;

			return new FieldType(merged,
					MapUtil.merge(HashMap::new, this.annotations, otherFieldType.annotations),
					MapUtil.merge(HashMap::new, this.globalAnnotations, otherFieldType.globalAnnotations));
		} else {
			Clz merged = this.clazz.map(other, types, mergeDirection);
			if (this.clazz.equals(merged)) return this;

			return new FieldType(merged, this.annotations, this.globalAnnotations);
		}
	}

	@Override
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

	@Override
	public Class<?> pullBytecodeClass() {
		return this.clazz.pullBytecodeClass();
	}

	@Override
	public Class<?> pullClass() {
		return this.clazz.pullClass();
	}
}
