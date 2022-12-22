package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;

/**
 * A SerializerDef is responsible for writing code that handles a given class. <br>
 * In some cases it may require a separate method and in those cases a {@link MethodDef} is used which inherits {@link SerializerDef}
 */
public interface SerializerDef {
	/**
	 * Writes code for encoding the Clazz the definition is designed to handle.
	 *
	 * @param mh        A MethodHandler
	 * @param valueLoad A Runnable which pushes the Clazz value onto the stack.
	 */
	void writePut(MethodHandler mh, Runnable valueLoad);

	/**
	 * Writes code for decoding the Clazz the definition is designed to handle.
	 *
	 * @param mh A MethodHandler
	 */
	void writeGet(MethodHandler mh);

	/**
	 * Writes code for measuring the size required to encode the value
	 *
	 * @param mh        A MethodHandler
	 * @param valueLoad A Runnable which pushes the Clazz value onto the stack.
	 */
	default void writeMeasure(MethodHandler mh, Runnable valueLoad) {
		if (!hasDynamicSize()) {
			mh.visitLdcInsn(getStaticSize());
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * This returns the absolute minimum size of the object. <br>
	 * If the size is dependent on the runtime object itself this value will be appended to the measure methods results.
	 *
	 * @return Object Minimum encoding size
	 */
	default long getStaticSize() {
		return 0;
	}

	/**
	 * If this is true it will request to generate the Measure method. <br>
	 * If this is false only the static size amount will be used.
	 *
	 * @return if it has a Dynamic Size.
	 */
	default boolean hasDynamicSize() {
		return true;
	}
}
