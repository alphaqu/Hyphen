package dev.quantumfusion.hyphen.scan.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.HashMap;
import java.util.Map;

public record AnnType(
		Clz clazz,
		Map<Class<? extends Annotation>, ? extends Annotation> annotations,
		Map<Class<? extends Annotation>, ? extends Annotation> globalAnnotations
) implements Clz {
	@Override
	public AnnType resolve(Clazz context) {
		Clz resolved = this.clazz.resolve(context);
		if (resolved == this.clazz)
			return this;
		return new AnnType(resolved, this.annotations, this.globalAnnotations);
	}

	@Override
	public AnnType map(Clz other, Map<TypeClazz, TypeClazz> types, MergeDirection mergeDirection) {
		if (other instanceof AnnType otherAnnType) {
			// idk what to do with the annotations
			Clz merged = this.clazz.map(otherAnnType.clazz, types, mergeDirection);
			if (otherAnnType.equals(merged) && this.annotations.isEmpty() && this.globalAnnotations.isEmpty())
				return otherAnnType;

			var annotations = new HashMap<Class<? extends Annotation>, Annotation>(this.annotations);
			var globalAnnotations = new HashMap<Class<? extends Annotation>, Annotation>(this.globalAnnotations);
			annotations.putAll(otherAnnType.annotations);
			globalAnnotations.putAll(otherAnnType.globalAnnotations);

			return new AnnType(merged, annotations, globalAnnotations);
		} else {
			Clz merged = this.clazz.map(other, types, mergeDirection);
			if (this.clazz.equals(merged))
				return this;

			return new AnnType(merged, this.annotations, this.globalAnnotations);
		}
	}

	@Override
	public void finish(AnnotatedType type, Clazz source) {
		this.clazz.finish(type, source);
	}

	@Override
	public String toString() {
		return "AnnType{" +
				this.clazz +
				(this.annotations.isEmpty() ? "" : ", annotations=" + this.annotations) +
				(this.globalAnnotations.isEmpty() ? "" : ", globalAnnotations=" + this.globalAnnotations) +
				'}';
	}
}
