package dev.quantumfusion.hyphen.scan.annotations;

import dev.quantumfusion.hyphen.codegen.def.BufferDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
@HyphenAnnotation
public @interface DataBufferType {
	BufferDef.BufferType value();
}
