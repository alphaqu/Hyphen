package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.info.ClassInfo;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.io.ArrayIO;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class CodegenHandler {
	final String name;
	final ClassWriter cw;
	private final IOMode io;

	public CodegenHandler(Class<?> ioClazz, String name) {
		this.io = IOMode.create(ioClazz);
		this.name = name;
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		this.cw.visit(V16, ACC_PUBLIC + ACC_FINAL, name, null, Type.getInternalName(Object.class), new String[]{Type.getInternalName(HyphenSerializer.class)});
	}

	public void createConstructor() {
		try (MethodHandler mh = MethodHandler.createVoid(this, this.io, ACC_PUBLIC, "<init>")) {
			mh.visitIntInsn(ALOAD, 0);
			mh.callSpecialMethod(Object.class, "<init>", Void.TYPE);
			mh.returnOp();
		}
	}

	public void createMethods(TypeInfo info, MethodMetadata methodMetadata) {
		createEncode(info, methodMetadata);
		createDecode(info, methodMetadata);
	}

	public void createEncode(TypeInfo info, MethodMetadata methodMetadata) {
		final boolean main = isMain(info);
		try (MethodHandler mh = MethodHandler.createVoid(
				this,
				this.io,
				(main ? 0 : ACC_STATIC) | ACC_PUBLIC | ACC_FINAL,
				main ? "encode" : Constants.PUT_FUNC + info.getMethodName(false),
				main ? Object.class : this.io.ioClass,
				main ? Object.class : info.getClazz())
		) {

			MethodHandler.Var io;
			MethodHandler.Var data;
			if (main) {
				mh.createVar("this", Object.class);
				var ioRaw = mh.createVar("ioRaw", Object.class);
				var dataRaw = mh.createVar("dataRaw", Object.class);
				io = mh.createVar("io", this.io.ioClass);
				data = mh.createVar("data", info.getClazz());

				ioRaw.load();
				mh.cast(this.io.ioClass);
				io.store();

				dataRaw.load();
				mh.cast(info.getClazz());
				data.store();
			} else {
				io = mh.createVar("io", this.io.ioClass);
				data = mh.createVar("data", info.getClazz());
			}
			methodMetadata.writePut(mh, io, data);
		}
	}

	public void createDecode(TypeInfo info, MethodMetadata methodMetadata) {
		final boolean main = isMain(info);
		try (MethodHandler mh = MethodHandler.create(
				this,
				this.io,
				(main ? 0 : ACC_STATIC) | ACC_PUBLIC | ACC_FINAL,
				main ? "decode" : Constants.GET_FUNC + info.getMethodName(false),
				main ? Object.class : info.getClazz(),
				main ? Object.class : this.io.ioClass)) {
			MethodHandler.Var io;
			if (main) {
				mh.createVar("this", Object.class);
				var ioRaw = mh.createVar("ioRaw", Object.class);
				io = mh.createVar("io", this.io.ioClass);
				ioRaw.load();
				mh.cast(this.io.ioClass);
				io.store();
			} else io = mh.createVar("io", this.io.ioClass);

			methodMetadata.writeGet(mh, io);
		}
	}

	private boolean isMain(TypeInfo info) {
		if (info instanceof ClassInfo classInfo)
			return classInfo.main;
		return false;
	}

	private void createIntCombine() {
		try (MethodHandler mh = MethodHandler.create(this, this.io, ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "combine", Void.TYPE, ArrayIO.class)) {
			var io = mh.createVar("io", this.io.ioClass);


			io.load();
			mh.visitLdcInsn(69);
			mh.visitLdcInsn(420);
			mh.callIOPut(long.class);
			mh.returnOp();
		}
	}

	public byte[] byteArray() {
		return this.cw.toByteArray();
	}

	public Class<?> export() {
		return new ClassLoader() {
			public Class<?> define(byte[] bytes, String name) {
				return super.defineClass(name, bytes, 0, bytes.length);
			}
		}.define(cw.toByteArray(), name);
	}

	@FunctionalInterface
	public interface UWUWU {
		void uwu(ArrayIO io, Integer i);
	}

	@FunctionalInterface
	public interface UWUWU2 {
		void uwu(Integer i);
	}

	private static class Loader extends ClassLoader {
		public Loader(ClassLoader parent) {
			super(parent);
		}

		public Class<?> define(String name, byte[] bytes) {
			return super.defineClass(name, bytes, 0, bytes.length);
		}
	}
}
