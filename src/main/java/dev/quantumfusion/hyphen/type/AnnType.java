package dev.quantumfusion.hyphen.type;

import java.lang.annotation.Annotation;
import java.util.Map;

public record AnnType(
		Clz clazz,
		Map<Class<? extends Annotation>, ? extends Annotation> annotations,
		Map<Class<? extends Annotation>, ? extends Annotation> globalAnnotations
) implements Clz {
	@Override
	public AnnType resolve(Clazz source) {
		Clz resolved = this.clazz.resolve(source);
		if (resolved == this.clazz)
			return this;
		return new AnnType(resolved, this.annotations, this.globalAnnotations);
	}

	@Override
	public String toString() {
		return "AnnType{" +
				"clazz=" + this.clazz +
				(this.annotations.isEmpty() ? "" : ", annotations=" + this.annotations) +
				(this.globalAnnotations.isEmpty() ? "" : ", globalAnnotations=" + this.globalAnnotations) +
				'}';
	}
}
