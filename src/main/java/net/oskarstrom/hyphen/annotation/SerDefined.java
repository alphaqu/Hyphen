package net.oskarstrom.hyphen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
public @interface SerDefined {
	String name();
	Class<?>[] values();
	// SerComplexSubClass[] poly() default {};
}
