package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.ClassDefiner;
import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.SerializerFactory;
import dev.quantumfusion.hyphen.codegen.def.*;
import dev.quantumfusion.hyphen.io.IOInterface;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.type.ArrayClazz;
import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.scan.type.TypeClazz;
import dev.quantumfusion.hyphen.scan.type.UnknownClazz;
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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
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
	public final Map<Object, Map<Class<? extends Annotation>, Object>> annotationProviders;
	private final EnumMap<Options, Boolean> options;
	private final Map<Clazz, SerializerDef> scanDeduplicationMap = new HashMap<>();
	private final Map<Clazz, MethodDef> methods = new HashMap<>();
	@Nullable
	private final Map<Class<?>[], AtomicInteger> methodDeduplication;
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
			Map<Object, Map<Class<? extends Annotation>, Object>> annotationProviders) {
		this.ioClass = ioClass;
		this.dataClass = dataClass;
		this.className = className;
		this.exportPath = exportPath;
		this.classLoader = classLoader;
		this.options = options;
		this.definitions = definitions;
		this.annotationProviders = annotationProviders;

		// Initialize the class generation
		this.methodDeduplication = this.options.get(Options.SHORT_METHOD_NAMES) ? new HashMap<>() : null;
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		this.cw.visit(V16, ACC_PUBLIC | ACC_FINAL, this.className, null, GenUtil.internal(Object.class), new String[]{GenUtil.internal(HyphenSerializer.class)});
		try (var mh = new MethodHandler(this.cw.visitMethod(ACC_PUBLIC, "<init>", GenUtil.methodDesc(Void.TYPE), null, null), this.className, dataClass, ioClass)) {
			mh.visitIntInsn(ALOAD, 0);
			mh.callInst(INVOKESPECIAL, Object.class, "<init>", Void.TYPE);
			mh.op(RETURN);
		}

		if (options.get(Options.FAST_ALLOC)) {
			try (var mh = new MethodHandler(this.cw.visitMethod(ACC_STATIC, "<clinit>", GenUtil.methodDesc(Void.TYPE), null, null), this.className, dataClass, ioClass)) {
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
	 * @param clazz The Clazz to serialize
	 * @return A Serializer Def to handle it.
	 */
	public SerializerDef acquireDef(Clazz clazz) {
		if (scanDeduplicationMap.containsKey(clazz)) {
			return scanDeduplicationMap.get(clazz);
		}

		if (clazz == UnknownClazz.UNKNOWN) {
			throw new UnknownTypeException("Type could not be identified",
					"Check the Path for the source of \"UNKNOWN\" which is when a type is not known");
		}

		if ((clazz instanceof TypeClazz t && (t.defined == UnknownClazz.UNKNOWN))) {
			throw new UnknownTypeException("Type " + t.typeName + " could not be identified",
					"Trace the path of \"" + t.typeName + "\" in the path below. And see if you can define that path.");
		}

		if (clazz instanceof TypeClazz t) {
			return this.acquireDef(t.getDefined());
		}


		var definedClass = clazz.getDefinedClass();
		SerializerDef def;
		if (definitions.containsKey(definedClass)) {
			def = definitions.get(definedClass).create(clazz);
		} else if (clazz.containsAnnotation(DataSubclasses.class)) {
			def = new SubclassDef(clazz, (Class<?>[]) clazz.getAnnotationValue(DataSubclasses.class));
		} else if (clazz instanceof ArrayClazz arrayClazz) {
			def = new ArrayDef(arrayClazz);
		} else if (clazz.getDefinedClass().isEnum()) {
			def = new EnumDef(clazz);
		} else {
			def = new ClassDef(clazz);
		}

		scanDeduplicationMap.put(clazz, def);
		if (def instanceof MethodDef methodDef) {
			methods.put(clazz, methodDef);
		}
		def.scan(this);
		return def;

	}

	/**
	 * Applies changes to MethodInfo. For example if method deduplication is used it will change the name on the method to the compact variant.
	 */
	public MethodInfo createMethodInfo(Clazz clazz, String prefix, String suffix, Class<?> returnClass, Class<?>... parameters) {
		String name = GenUtil.makeSafe(prefix + clazz.toString() + suffix);
		if (methodDeduplication != null) {
			name = GenUtil.hyphenShortMethodName(methodDeduplication.computeIfAbsent(ArrayUtil.combine(
					new Class[]{returnClass},
					parameters
			), info1 -> new AtomicInteger(0)).getAndIncrement());
		}
		return new MethodInfo(name, returnClass, parameters);
	}

	public void generateMethod(Clazz clazz, MethodInfo methodInfo, boolean synthetic, Consumer<MethodHandler> writer) {
		final Class<?>[] parameters = methodInfo.parameters;
		try (var mh = new MethodHandler(cw, methodInfo, className, dataClass, ioClass, options.get(Options.SHORT_VARIABLE_NAMES), synthetic)) {
			for (int i = 0; i < parameters.length; i++) {
				mh.addVar(MethodHandler.getParamName(i), parameters[i]);
			}
			mh.visitCode();
			try {
				writer.accept(mh);
			} catch (Throwable thr) {
				throw HyphenException.rethrow(clazz, null, thr);
			}
			mh.op(Type.getType(methodInfo.returnClass).getOpcode(IRETURN));
			mh.visitEnd();
		} catch (Throwable throwable) {
			throw HyphenException.rethrow(clazz, "hyphenMethod " + methodInfo.name, throwable);
		}
	}

	public boolean isEnabled(Options option) {
		return this.options.get(option);
	}

	public HyphenSerializer<IO, D> build() {
		try {
			final Clazz clazz = Clazz.create(this.dataClass);
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
