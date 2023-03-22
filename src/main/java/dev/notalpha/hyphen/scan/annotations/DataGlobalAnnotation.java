package dev.notalpha.hyphen.scan.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PACKAGE})
@HyphenAnnotation
public @interface DataGlobalAnnotation {
	/**
	 * Global Annotation id
	 */
	String value();
}
