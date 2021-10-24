package dev.quantumfusion.hyphen;

public class ClassDefiner extends ClassLoader {
	public static HyphenSerializer<?, ?> SERIALIZER;

	public ClassDefiner(ClassLoader parent) {
		super(parent);
	}

	public Class<?> def(String name, byte[] bytes) {
		final Class<?> aClass = defineClass(name, bytes, 0, bytes.length, null);
		try {
			Class.forName(name, true, this);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return aClass;
	}
}
