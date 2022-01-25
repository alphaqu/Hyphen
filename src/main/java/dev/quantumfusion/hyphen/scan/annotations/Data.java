package dev.quantumfusion.hyphen.scan.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
@HyphenAnnotation
@Deprecated(forRemoval = true)
public @interface Data {
}

