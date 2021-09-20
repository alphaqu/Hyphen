package dev.quantumfusion.hyphen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FailTest {
	// Throwable can never be thrown itself, so this will force a test to always fail
	Class<? extends Throwable> value() default Throwable.class;
}
