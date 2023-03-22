package dev.notalpha.hyphen.codegen;


import dev.notalpha.hyphen.HyphenSerializer;
import dev.notalpha.hyphen.SerializerGenerator;
import dev.notalpha.hyphen.scan.struct.Struct;

/**
 * This defines a methods properties like parameters and what the method returns. <br>
 * Never call methods by their name but instead call {@link HyphenSerializer} methods by calling
 * {@link MethodWriter#callInst(MethodInfo)} <br><br>
 * <p>
 */
public class MethodInfo {
	public final Class<?> returnClass;
	public final Class<?>[] parameters;
	public final String name;

	/**
	 * DO NOT USE THIS.
	 * <br><br>
	 * USE {@link SerializerGenerator#createMethodInfo(Struct, String, String, Class, Class[])}} INSTEAD
	 */
	public MethodInfo(String name, Class<?> returnClass, Class<?>... parameters) {
		this.name = name;
		this.returnClass = returnClass;
		this.parameters = parameters;
	}
}
