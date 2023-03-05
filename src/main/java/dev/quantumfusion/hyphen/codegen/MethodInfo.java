package dev.quantumfusion.hyphen.codegen;


import dev.quantumfusion.hyphen.scan.struct.Struct;

/**
 * This defines a methods properties like parameters and what the method returns. <br>
 * Never call methods by their name but instead call {@link dev.quantumfusion.hyphen.HyphenSerializer} methods by calling
 * {@link MethodWriter#callInst(MethodInfo)} <br><br>
 * <p>
 */
public class MethodInfo {
	public final Class<?> returnClass;
	public final Class<?>[] parameters;
	public final String name;

	/**
	 * <h2>DO NOT USE THIS.</h2>
	 * USE {@link dev.quantumfusion.hyphen.SerializerGenerator#createMethodInfo(Struct, String, String, Class, Class[])}} INSTEAD
	 */
	public MethodInfo(String name, Class<?> returnClass, Class<?>... parameters) {
		this.name = name;
		this.returnClass = returnClass;
		this.parameters = parameters;
	}
}
