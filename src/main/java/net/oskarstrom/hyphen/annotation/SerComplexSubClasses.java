package net.oskarstrom.hyphen.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
public @interface SerComplexSubClasses {
	SerComplexSubClass[] value();
}
