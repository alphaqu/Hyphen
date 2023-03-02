package dev.quantumfusion.hyphen.codegen;


/**
 * This defines a methods properties like parameters and what the method returns. <br>
 * Never call methods by their name but instead call {@link dev.quantumfusion.hyphen.HyphenSerializer} methods by calling
 * {@link MethodHandler#callInst(MethodInfo)} <br><br>
 * <p>
 */
public class MethodInfo {
	public final Class<?> returnClass;
	public final Class<?>[] parameters;
	public final String name;

	MethodInfo(String name, Class<?> returnClass, Class<?>... parameters) {
		this.name = name;
		this.returnClass = returnClass;
		this.parameters = parameters;
	}
}
