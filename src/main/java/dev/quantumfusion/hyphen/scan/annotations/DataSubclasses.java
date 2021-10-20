package dev.quantumfusion.hyphen.scan.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
@HyphenAnnotation
public @interface DataSubclasses {
	Class<?>[] value();
}
