package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.gen.*;
import dev.quantumfusion.hyphen.gen.impl.AbstractDef;
import dev.quantumfusion.hyphen.gen.impl.IntDef;
import dev.quantumfusion.hyphen.gen.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.IllegalClassException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
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
		byte[] build = debug.build();
		CheckClassAdapter.verify(new ClassReader(build), null, true, new PrintWriter(System.out));

		Files.write(Path.of("C:\\Program Files (x86)\\inkscape\\MinecraftMods\\Hyphen\\thing.class"), build);
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
			public void writeEncode(MethodVisitor methodVisitor, TypeInfo parent, FieldEntry fieldEntry, Context context) {
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

	public byte[] build() {
		if (clazz.getTypeParameters().length > 0) {
			throw ThrowHandler.fatal(IllegalClassException::new, "The Input class has Parameters,");
		}
		ScanHandler scanner = new ScanHandler(methods, implementations, subclasses, debug);
		scanner.scan(clazz);


		SerializerClassFactory serializerClassFactory = new SerializerClassFactory(implementations, IOMode.UNSAFE);
		methods.forEach(serializerClassFactory::createMethod);
		return serializerClassFactory.compileCode();
	}


	public enum Thigns {
		FUCK,
		S,
		GOUBER
	}

	public static class Test {
		@Serialize
		public Component[][] thinbruh;

		public Test(Component[][] thinbruh) {
			this.thinbruh = thinbruh;
		}
	}

	public static class Component {
		@Serialize
		public int thinbruh;

		public Component(int thinbruh) {
			this.thinbruh = thinbruh;
		}
	}
}
