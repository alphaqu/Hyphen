package net.oskarstrom.hyphen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Serialize {
	/**
	 * If the value is nullable
	 * @return nullable
	 */
	boolean value() default false;
}
