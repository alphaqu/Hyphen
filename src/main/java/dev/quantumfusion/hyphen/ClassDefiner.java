package dev.quantumfusion.hyphen;

public class ClassDefiner extends ClassLoader {
	public static HyphenSerializer<?, ?> SERIALIZER;

	public ClassDefiner(ClassLoader parent) {
		super(parent);
	}

	public Class<?> def(String name, byte[] bytes) {
		return defineClass(name, bytes, 0, bytes.length, null);
	}
}
