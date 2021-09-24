package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.gen.FieldEntry;
import dev.quantumfusion.hyphen.gen.IOMode;
import dev.quantumfusion.hyphen.gen.SerializerClassFactory;
import dev.quantumfusion.hyphen.gen.impl.AbstractDef;
import dev.quantumfusion.hyphen.gen.impl.IntDef;
import dev.quantumfusion.hyphen.gen.impl.ObjectSerializationDef;
import dev.quantumfusion.hyphen.gen.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.IllegalClassException;
import org.objectweb.asm.MethodVisitor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

public class SerializerFactory {
	private final Map<Class<?>, Function<? super TypeInfo, ? extends ObjectSerializationDef>> implementations = new HashMap<>();
	private final Map<TypeInfo, SerializerMetadata> methods = new LinkedHashMap<>();
	private final Map<Object, List<Class<?>>> subclasses = new HashMap<>();
	private final boolean debug;
	private final Class<?> clazz;

	protected SerializerFactory(boolean debug, Class<?> clazz) {
		this.debug = debug;
		this.clazz = clazz;
	}

	public static SerializerFactory create(Class<?> clazz) {
		return createInternal(false, clazz);
	}

	public static SerializerFactory createDebug(Class<?> clazz) {
		return createInternal(true, clazz);
	}

	private static SerializerFactory createInternal(boolean debugMode, Class<?> clazz) {
		final SerializerFactory scanHandler = new SerializerFactory(debugMode, clazz);
		scanHandler.addImpl(int.class, (field) -> new IntDef());
		return scanHandler;
	}

	public static void main(String[] args) throws IOException {

		Thigns gouber = Thigns.GOUBER;
		SerializerFactory debug = SerializerFactory.createDebug(Test.class);


		Component[][] thinbruh = new Component[5][];
		for (int i = 0; i < thinbruh.length; i++) {
			thinbruh[i] = new Component[]{new Component(23), new Component(32)};
		}
		Test test = new Test(420, thinbruh, 69);
		Class<?> build1 = debug.build();
		try {
			ByteBufferIO byteBufferIO = ByteBufferIO.create(1000);

			build1.getDeclaredMethod("Test_encode", Test.class, ByteBufferIO.class).invoke(null, test, byteBufferIO);
			byteBufferIO.rewind();
			Test test_decode = (Test) build1.getDeclaredMethod("Test_decode", ByteBufferIO.class).invoke(null, byteBufferIO);

			if (test.equals(test_decode)) {
				System.out.println("WE FUCKING DID IT");
			}

		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}

	}

	public void addSubclasses(Class<?> clazz, Class<?>... subclass) {
		subclasses.computeIfAbsent(clazz, c -> new ArrayList<>()).addAll(Arrays.asList(subclass));
	}

	public void addSubclassKeys(String key, Class<?>... subclass) {
		subclasses.computeIfAbsent(key, c -> new ArrayList<>()).addAll(Arrays.asList(subclass));
	}

	public void addImpl(Class<?> clazz, Function<? super TypeInfo, ? extends ObjectSerializationDef> creator) {
		this.implementations.put(clazz, creator);
	}

	public void addTestImpl(Class<?>... clazz) {
		for (Class<?> aClass : clazz) {
			this.addTestImpl(aClass);
		}
	}

	public void addTestImpl(Class<?> clazz) {
		this.addImpl(clazz, (field) -> new AbstractDef() {
			@Override
			public Class<?> getType() {
				return clazz;
			}

			@Override
			public void writeEncode(MethodVisitor methodVisitor, TypeInfo parent, FieldEntry fieldEntry, Context context, Runnable alloc) {
			}

			@Override
			public void writeDecode(MethodVisitor methodVisitor, TypeInfo parent, FieldEntry fieldEntry, Context ctx) {
			}

			@Override
			public String toString() {
				return "FakeTestDef" + clazz.getSimpleName();
			}
		});
	}

	private byte[] buildCode() {
		if (clazz.getTypeParameters().length > 0) {
			throw ThrowHandler.fatal(IllegalClassException::new, "The Input class has Parameters,");
		}
		ScanHandler scanner = new ScanHandler(methods, implementations, subclasses, debug);
		scanner.scan(clazz);


		SerializerClassFactory serializerClassFactory = new SerializerClassFactory(implementations, IOMode.BYTEBUFFER);
		methods.forEach(serializerClassFactory::createMethod);
		return serializerClassFactory.compileCode();
	}

	public Class<?> build() {
		if (clazz.getTypeParameters().length > 0) {
			throw ThrowHandler.fatal(IllegalClassException::new, "The Input class has Parameters,");
		}
		ScanHandler scanner = new ScanHandler(methods, implementations, subclasses, debug);
		scanner.scan(clazz);


		SerializerClassFactory serializerClassFactory = new SerializerClassFactory(implementations, IOMode.BYTEBUFFER);
		methods.forEach(serializerClassFactory::createMethod);
		return serializerClassFactory.compile();
	}


	public enum Thigns {
		FUCK,
		S,
		GOUBER
	}

	public static class Test {
		@Serialize
		public int thinbruh2;
		@Serialize
		public Component[][] thinbruh;
		@Serialize
		public int thinbruh3;

		public Test(int thinbruh2, Component[][] thinbruh, int thinbruh3) {
			this.thinbruh2 = thinbruh2;
			this.thinbruh = thinbruh;
			this.thinbruh3 = thinbruh3;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Test)) return false;
			Test test = (Test) o;
			return thinbruh2 == test.thinbruh2 && thinbruh3 == test.thinbruh3 && Arrays.deepEquals(thinbruh, test.thinbruh);
		}

		@Override
		public int hashCode() {
			int result = Objects.hash(thinbruh2, thinbruh3);
			result = 31 * result + Arrays.deepHashCode(thinbruh);
			return result;
		}
	}

	public static class Component {
		@Serialize
		public int thinbruh;

		public Component(int thinbruh) {
			this.thinbruh = thinbruh;
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Component component)) return false;
			return thinbruh == component.thinbruh;
		}

		@Override
		public int hashCode() {
			return Objects.hash(thinbruh);
		}
	}
}
