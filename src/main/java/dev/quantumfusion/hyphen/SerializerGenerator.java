package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.codegen.MethodWriter;
import dev.quantumfusion.hyphen.codegen.MethodInfo;
import dev.quantumfusion.hyphen.codegen.def.*;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.scan.StructScanner;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.struct.*;
import dev.quantumfusion.hyphen.thr.HyphenException;
import dev.quantumfusion.hyphen.thr.UnknownTypeException;
import dev.quantumfusion.hyphen.util.ArrayUtil;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

/**
 * The Actual generation logic. For usage use {@link SerializerFactory} instead.
 *
 * @param <IO> IO Class
 * @param <D>  Data Class
 */
public class SerializerGenerator<IO extends IOInterface, D> {
	public final Class<IO> ioClass;
	public final Class<D> dataClass;
	public final String className;
	public final Path exportPath;
	public final Map<Class<?>, SerializerFactory.DynamicDefFactory> definitions;
	private final EnumMap<Options, Boolean> options;
	public final StructScanner scanner;
	private final Map<Struct, SerializerDef> scanDeduplicationMap = new HashMap<>();
	private final Map<Struct, MethodDef> methods = new HashMap<>();
	@Nullable
	private final Map<List<Class<?>>, AtomicInteger> methodDeduplication;
	private final ClassWriter cw;
	private final ClassLoader classLoader;

	public SerializerGenerator(
			Class<IO> ioClass,
			Class<D> dataClass,
			String className,
			Path exportPath,
			ClassLoader classLoader,
			EnumMap<Options, Boolean> options,
			Map<Class<?>, SerializerFactory.DynamicDefFactory> definitions,
			Map<Object, List<Annotation>> annotationProviders) {
		this.ioClass = ioClass;
		this.dataClass = dataClass;
		this.className = className;
		this.exportPath = exportPath;
		this.classLoader = classLoader;
		this.options = options;
		this.definitions = definitions;
		this.scanner = new StructScanner(annotationProviders);

		// Initialize the class generation
		this.methodDeduplication = new HashMap<>();
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		this.cw.visit(V16, ACC_PUBLIC | ACC_FINAL, this.className, null, GenUtil.internal(Object.class), new String[]{GenUtil.internal(HyphenSerializer.class)});
		try (var mh = new MethodWriter(this.cw.visitMethod(ACC_PUBLIC, "<init>", GenUtil.methodDesc(Void.TYPE), null, null), this.className, dataClass, ioClass)) {
			mh.visitIntInsn(ALOAD, 0);
			mh.callInst(INVOKESPECIAL, Object.class, "<init>", Void.TYPE);
			mh.op(RETURN);
		}

		if (options != null && options.get(Options.FAST_ALLOC)) {
			try (var mh = new MethodWriter(this.cw.visitMethod(ACC_STATIC, "<clinit>", GenUtil.methodDesc(Void.TYPE), null, null), this.className, dataClass, ioClass)) {
				mh.visitTypeInsn(NEW, this.className);
				mh.op(DUP);
				mh.visitMethodInsn(INVOKESPECIAL, this.className, "<init>", GenUtil.methodDesc(Void.TYPE), false);
				mh.visitFieldInsn(PUTSTATIC, ClassDefiner.class, "SERIALIZER", HyphenSerializer.class);
				mh.op(RETURN);
			}
		}
	}

	/**
	 * Acquires a definition for the given Clazz
	 *
	 * @param struct The Clazz to serialize
	 * @return A Serializer Def to handle it.
	 */
	public SerializerDef<?> acquireDef(Struct struct) {
		struct = struct.getValueStruct();
		if (scanDeduplicationMap.containsKey(struct)) {
			return scanDeduplicationMap.get(struct);
		}

		SerializerDef<?> def;
		try {
			var valueClass = struct.getValueClass();
			var definition = definitions.get(valueClass);
			if (definition != null) {
				def = definition.create(struct);
			} else if (struct.isAnnotationPresent(DataSubclasses.class)) {
				def = new SubclassDef(struct, struct.getAnnotation(DataSubclasses.class).value());
			} else if (struct instanceof ClassStruct classStruct) {
				if (struct.getValueClass().isEnum()) {
					def = new EnumDef(classStruct);
				} else if (classStruct.equals(ClassStruct.OBJECT)) {
					throw new HyphenException("Cannot serialize unknown type.", "Try to find a way to give hyphen information about what type this is.");
				} else {
					def = new ClassDef(classStruct);
				}
			} else if (struct instanceof ArrayStruct arrayStruct) {
				def = new ArrayDef(arrayStruct);
			} else if (struct instanceof WildcardStruct wildcardStruct) {
				DataSubclasses annotation = wildcardStruct.getAnnotation(DataSubclasses.class);

				if (annotation == null) {
					throw new HyphenException("Wildcard type without a @DataSubclasses annotation.", "Add a list of subclasses");
				}
				def = new SubclassDef(wildcardStruct, annotation.value());
			} else {
				throw new IllegalStateException();
			}

			scanDeduplicationMap.put(struct, def);
			if (def instanceof MethodDef<?> methodDef) {
				methods.put(struct, methodDef);
			}
			def.scan(this);
		} catch (Throwable throwable) {
			throw HyphenException.rethrow(struct, "scanning", throwable);
		}
		return def;

	}

	/**
	 * Applies changes to MethodInfo. For example if method deduplication is used it will change the name on the method to the compact variant.
	 */
	public MethodInfo createMethodInfo(Struct struct, String prefix, String suffix, Class<?> returnClass, Class<?>... parameters) {
		String name = GenUtil.makeSafe(prefix + struct.toString() + suffix);
		if (methodDeduplication != null) {
			name = GenUtil.hyphenShortMethodName(methodDeduplication.computeIfAbsent(Arrays.asList(ArrayUtil.combine(
					new Class[]{returnClass},
					parameters
			)),info1 -> new AtomicInteger(0)).getAndIncrement());
		}
		return new MethodInfo(name, returnClass, parameters);
	}

	public void generateMethod(Struct struct, MethodInfo methodInfo, boolean synthetic, Consumer<MethodWriter> writer) {
		final Class<?>[] parameters = methodInfo.parameters;
		try (var mh = new MethodWriter(cw, methodInfo, className, dataClass, ioClass, options.get(Options.SHORT_VARIABLE_NAMES), synthetic)) {
			for (int i = 0; i < parameters.length; i++) {
				mh.addVar(MethodWriter.getParamName(i), parameters[i]);
			}
			mh.visitCode();
			try {
				writer.accept(mh);
			} catch (Throwable thr) {
				throw HyphenException.rethrow(struct, null, thr);
			}
			mh.op(Type.getType(methodInfo.returnClass).getOpcode(IRETURN));
			mh.visitEnd();
		} catch (Throwable throwable) {
			throw HyphenException.rethrow(struct, "hyphenMethod " + methodInfo.name, throwable);
		}
	}

	public boolean isEnabled(Options option) {
		return this.options.get(option);
	}

	public HyphenSerializer<IO, D> build() {
		try {
			final Struct clazz = new ClassStruct(this.dataClass);
			final SerializerDef def = acquireDef(clazz);
			MethodDef methodDef;
			if (def instanceof MethodDef) {
				methodDef = (MethodDef) def;
			} else {
				throw new RuntimeException("Data class does not have a method definition");
			}

			// Safety check if all the definitions have been initialized.
			this.scanDeduplicationMap.forEach((clazz1, serializerDef) -> {
				if (!serializerDef.isScanned()) {
					throw new HyphenException(serializerDef + " had never scan() called", "Call super.scan() on the implementation.");
				}
			});

			Consumer<MethodVisitor> throwException = (visitor) -> {
				String exception = Type.getInternalName(UnsupportedOperationException.class);
				visitor.visitTypeInsn(NEW, exception);
				visitor.visitInsn(DUP);
				visitor.visitMethodInsn(INVOKESPECIAL, exception, "<init>", Type.getMethodDescriptor(
						Type.getType(Void.TYPE)
				), false);
				visitor.visitInsn(ATHROW);
			};


			// Create sparks
			this.generateSpark(
					new MethodInfo("get", Object.class, IOInterface.class),
					methodDef.getInfo(),
					(visitor) -> {
					},
					throwException
			);
			this.generateSpark(
					new MethodInfo("put", Void.TYPE, IOInterface.class, Object.class),
					methodDef.putInfo(),
					(visitor) -> {},
					throwException
			);

			long staticSize = methodDef.getStaticSize();
			this.generateSpark(
					new MethodInfo("measure", long.class, Object.class),
					methodDef.measureInfo(),
					(visitor) -> {
						visitor.visitLdcInsn(staticSize);
						visitor.visitInsn(LADD);
					},
					(visitor) -> {
						if (this.options.get(Options.DISABLE_MEASURE)) {
							throwException.accept(visitor);
						} else {
							// In this case the measure info is null because the measurer does not have a dynamic size.
							visitor.visitLdcInsn(staticSize);
							visitor.visitInsn(LRETURN);
						}
					}
			);

			// Generate methods
			this.methods.forEach((childClazz, childMethodDef) -> {
				childMethodDef.generateMethods(this);
			});

			// Define class
			return defineClass();
		} catch (Throwable throwable) {
			HyphenException hyphenException;
			if (throwable instanceof HyphenException he) {
				hyphenException = he;
			} else {
				hyphenException = new HyphenException(throwable, null);
			}
			throw hyphenException;
		}
	}

	private synchronized <O extends HyphenSerializer<IO, D>> HyphenSerializer<IO, D> defineClass() {
		final byte[] bytes = cw.toByteArray();

		try {
			if (exportPath != null) {
				try {
					Files.write(exportPath, bytes, StandardOpenOption.CREATE);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ClassDefiner definer = new ClassDefiner(classLoader);
			final Class<O> def = (Class<O>) definer.def(className, bytes);

			if (options.get(Options.FAST_ALLOC)) {
				return (HyphenSerializer<IO, D>) ClassDefiner.SERIALIZER;
			} else {
				try {
					var constructor = def.getConstructor();
					constructor.setAccessible(true);
					return constructor.newInstance();
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			}
		} catch (Throwable thr) {
			CheckClassAdapter.verify(new ClassReader(bytes), true, new PrintWriter(System.out));
			throw thr;
		}
	}

	private void generateSpark(MethodInfo info, MethodInfo target, Consumer<MethodVisitor> postProcessor, Consumer<MethodVisitor> nullWriter) {
		MethodVisitor visitor = cw.visitMethod(ACC_PUBLIC | ACC_FINAL, info.name,
				GenUtil.methodDesc(info.returnClass, info.parameters)
				, null, null);

		visitor.visitCode();
		if (target != null) {
			for (int i = 0; i < info.parameters.length; i++) {
				var targetParameter = target.parameters[i];
				visitor.visitVarInsn(Type.getType(targetParameter).getOpcode(ILOAD), i + 1);
				visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(targetParameter));
			}

			visitor.visitMethodInsn(INVOKESTATIC, className, target.name, GenUtil.methodDesc(target.returnClass, target.parameters), false);
			if (!target.returnClass.isPrimitive()) {
				visitor.visitTypeInsn(CHECKCAST, Type.getInternalName(Object.class));
			}
			postProcessor.accept(visitor);
			visitor.visitInsn(Type.getType(target.returnClass).getOpcode(IRETURN));
		} else {
			nullWriter.accept(visitor);
		}

		visitor.visitMaxs(0, 0);
		visitor.visitEnd();
	}
}
