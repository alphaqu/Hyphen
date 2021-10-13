package dev.quantumfusion.hyphen.type;

import dev.quantumfusion.hyphen.Direction;

import java.lang.annotation.Annotation;
import java.lang.reflect.WildcardType;

public class WildClazz extends Clazz {


	public WildClazz(Class<?> aClass) {
		super(aClass, new Annotation[0]);
	}

	public static WildClazz create(WildcardType wild, Clazz clz, Direction dir) {
		return new WildClazz(Object.class);
	}
}
