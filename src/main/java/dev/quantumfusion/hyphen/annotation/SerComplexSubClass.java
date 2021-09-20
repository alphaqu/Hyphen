package dev.quantumfusion.hyphen.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
@HyphenOptionAnnotation
@Repeatable(SerComplexSubClasses.class)
public @interface SerComplexSubClass {
	Class<?> value();

	SerDefined[] types() default {};

}
