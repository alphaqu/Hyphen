package dev.quantumfusion.hyphen.scan.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@HyphenAnnotation
@Target({ElementType.TYPE, ElementType.PACKAGE, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataNullable {
}
