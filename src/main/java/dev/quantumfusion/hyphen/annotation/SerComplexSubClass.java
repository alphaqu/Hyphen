package dev.quantumfusion.hyphen.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
@HyphenAnnotation
@Repeatable(SerComplexSubClasses.class)
public @interface SerComplexSubClass {
	Class<?> value();

	SerDefined[] types() default {};

}
