package dev.notalpha.hyphen;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FailTest {
	Class<? extends Throwable> value() default Throwable.class;

	String msg() default "";
}
