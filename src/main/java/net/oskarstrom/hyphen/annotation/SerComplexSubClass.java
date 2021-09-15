package net.oskarstrom.hyphen.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
@Repeatable(SerComplexSubClasses.class)
public @interface SerComplexSubClass {
	Class<?> value();

	SerDefined[] types() default {};
}
