package dev.quantumfusion.hyphen.scan.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Repeatable(DataOptionStack.class)
public @interface DataOption {
	String key();
	String value() default "true";
}
