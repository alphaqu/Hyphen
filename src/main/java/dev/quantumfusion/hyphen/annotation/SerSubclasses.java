package dev.quantumfusion.hyphen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE_USE)
public @interface SerSubclasses {
	Class<?>[] value();
}
