package dev.quantumfusion.hyphen.util;

import java.lang.reflect.Type;

public interface ArrayType extends Type {

	Class<?> getComponentType();

	Class<?> getType();

	class ArrayTypeImpl implements ArrayType {
		private final Class<?> componentType;
		private final Class<?> type;

		public ArrayTypeImpl(Class<?> componentType, Class<?> type) {
			this.componentType = componentType;
			this.type = type;
		}

		public Class<?> getComponentType() {
			return componentType;
		}

		public Class<?> getType() {
			return type;
		}
	}
}
