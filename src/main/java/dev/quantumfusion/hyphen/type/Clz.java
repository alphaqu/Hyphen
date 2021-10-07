package dev.quantumfusion.hyphen.type;

import java.lang.reflect.AnnotatedType;

public interface Clz {
	Clz resolve(Clazz source);

	default Clz instantiate(AnnotatedType annotatedType){
		return this;
	}

	default void finish(AnnotatedType type, Clazz source){}
}
