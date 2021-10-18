package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.Direction;

import java.lang.annotation.Annotation;
import java.lang.reflect.WildcardType;
import java.util.Map;

public class WildClazz extends Clazz {
	public WildClazz(Class<?> aClass) {
		super(aClass, Map.of());
	}

	public static WildClazz create(WildcardType wild, Clazz clz, Direction dir) {
		return new WildClazz(Object.class);
	}
}
