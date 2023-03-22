package dev.notalpha.hyphen.codegen.def;

import dev.notalpha.hyphen.SerializerGenerator;
import dev.notalpha.hyphen.codegen.MethodWriter;
import dev.notalpha.hyphen.scan.struct.Struct;

/**
 * A SerializerDef is responsible for writing code that handles a given class. <br>
 * In some cases it may require a separate method and in those cases a {@link MethodDef} is used which inherits {@link SerializerDef}
 */
public abstract class SerializerDef<D extends Struct> {
	public final D struct;
	private boolean isScanned = false;
	protected SerializerDef(D struct) {
		this.struct = struct;
	}

	/**
	 * The scan method exposes the SerializerHandler for finding other SerializerDefs for use in the current SerializerDef
	 * @param handler 	A SerializerHandler
	 */
	public void scan(SerializerGenerator<?, ?> handler) {
		this.isScanned = true;
	}

	/**
	 * Writes code for encoding the Clazz the definition is designed to handle.
	 *
	 * @param mh        A MethodHandler
	 * @param valueLoad A Runnable which pushes the Clazz value onto the stack.
	 */
	public abstract void writePut(MethodWriter mh, Runnable valueLoad);

	/**
	 * Writes code for decoding the Clazz the definition is designed to handle.
	 *
	 * @param mh A MethodHandler
	 */
	public abstract void writeGet(MethodWriter mh);

	/**
	 * Writes code for measuring the size required to encode the value
	 *
	 * @param mh        A MethodHandler
	 * @param valueLoad A Runnable which pushes the Clazz value onto the stack.
	 */
	public void writeMeasure(MethodWriter mh, Runnable valueLoad) {
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
	public long getStaticSize() {
		return 0;
	}

	/**
	 * If this is true it will request to generate the Measure method. <br>
	 * If this is false only the static size amount will be used.
	 *
	 * @return if it has a Dynamic Size.
	 */
	public boolean hasDynamicSize() {
		return true;
	}

	public boolean isScanned() {
		return isScanned;
	}
}
