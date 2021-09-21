package dev.quantumfusion.hyphen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@HyphenOptionAnnotation
public @interface SerSubclasses {
	Class<?>[] value();
	String key() default "";
	//overrides existing subclass mappings on only using the key
	boolean override() default false;
}
