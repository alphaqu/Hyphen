package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.gen.Context;
import dev.quantumfusion.hyphen.gen.FieldEntry;
import dev.quantumfusion.hyphen.gen.IOMode;
import dev.quantumfusion.hyphen.gen.SerializerClassFactory;
import dev.quantumfusion.hyphen.gen.impl.AbstractDef;
import dev.quantumfusion.hyphen.gen.impl.IOArrayDef;
import dev.quantumfusion.hyphen.gen.impl.IOPrimDef;
import dev.quantumfusion.hyphen.gen.impl.ObjectSerializationDef;
import dev.quantumfusion.hyphen.gen.metadata.ClassSerializerMetadata;
import dev.quantumfusion.hyphen.gen.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.io.UnsafeIO;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.IllegalClassException;
import org.objectweb.asm.MethodVisitor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
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
		final SerializerFactory sh = new SerializerFactory(debugMode, clazz);
		sh.addImpl(IOPrimDef.create(boolean.class));
		sh.addImpl(IOPrimDef.create(byte.class));
		sh.addImpl(IOPrimDef.create(char.class));
		sh.addImpl(IOPrimDef.create(short.class));
		sh.addImpl(IOPrimDef.create(int.class));
		sh.addImpl(IOPrimDef.create(long.class));
		sh.addImpl(IOPrimDef.create(float.class));
		sh.addImpl(IOPrimDef.create(double.class));
		sh.addImpl(IOPrimDef.create(String.class));

		sh.addImpl(IOArrayDef.create(boolean[].class));
		sh.addImpl(IOArrayDef.create(byte[].class));
		sh.addImpl(IOArrayDef.create(char[].class));
		sh.addImpl(IOArrayDef.create(short[].class));
		sh.addImpl(IOArrayDef.create(int[].class));
		sh.addImpl(IOArrayDef.create(long[].class));
		sh.addImpl(IOArrayDef.create(float[].class));
		sh.addImpl(IOArrayDef.create(double[].class));
		sh.addImpl(IOArrayDef.create(String[].class));
		return sh;
	}

	public static void main(String[] args) throws IOException, URISyntaxException {

		SerializerFactory debug = SerializerFactory.createDebug(TestClass.class);


		var test = TestClass.create();

		Class<?> build1 = debug.build();

		var byteBufferIO = UnsafeIO.create(10000);
		try {
			for (int i = 0; i < 1; i++) {
				byteBufferIO.rewind();
				test.thinbruh2 = i;

				if (ClassSerializerMetadata.MODE == 0) {
					build1.getDeclaredMethod("TestClass_encode", TestClass.class, UnsafeIO.class).invoke(null, test, byteBufferIO);
				} else {
					build1.getDeclaredMethod("TestClass_encode", UnsafeIO.class, TestClass.class).invoke(null, byteBufferIO, test);
				}
				byteBufferIO.rewind();
				TestClass test_decode = (TestClass) build1.getDeclaredMethod("TestClass_decode", UnsafeIO.class).invoke(null, byteBufferIO);

				if (test.equals(test_decode) /*&& Math.random() == 2*/) {
					System.out.println("WE FUCKING DID IT");
				}
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

	public void addImpl(ObjectSerializationDef def) {
		this.implementations.put(def.getType(), (f) -> def);
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
			public void writeEncode2(MethodVisitor methodVisitor, Context ctx) {

			}

			@Override
			public void writeDecode2(MethodVisitor methodVisitor, Context ctx) {

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


		SerializerClassFactory serializerClassFactory = new SerializerClassFactory(implementations, IOMode.UNSAFE);
		methods.forEach(serializerClassFactory::createMethod);
		return serializerClassFactory.compile();
	}
}
