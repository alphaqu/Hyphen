package dev.quantumfusion.hyphen;

public @interface FailTest {
	Class<? extends Throwable> value() default Throwable.class;

	String msg() default "";
}
