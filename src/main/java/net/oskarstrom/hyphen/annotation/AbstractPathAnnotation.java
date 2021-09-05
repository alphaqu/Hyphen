package net.oskarstrom.hyphen.annotation;

public abstract @interface AbstractPathAnnotation {
	int[] path() default {};
}
