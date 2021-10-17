package dev.quantumfusion.hyphen;

public class ClassDefiner extends ClassLoader {
	public static HyphenSerializer<?, ?> SERIALIZER;

	public ClassDefiner(ClassLoader parent) {
		super(parent);
	}

	public Class<?> def(String name, byte[] bytes) {
		try {
			return defInternal(name, bytes);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private Class<?> defInternal(String name, byte[] bytes) throws ClassNotFoundException {
		Class<?> aClass = defineClass(name, bytes, 0, bytes.length, null);
		Class.forName(name, true, this);
		return aClass;
	}
}
