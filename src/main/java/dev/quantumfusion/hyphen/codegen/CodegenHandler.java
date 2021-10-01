package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.io.ArrayIO;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.objectweb.asm.Opcodes.*;

public class CodegenHandler {
	private final IOMode io;
	final String name;
	final ClassWriter cw;

	public CodegenHandler(Class<?> ioClazz, String name) {
		this.io = IOMode.create(ioClazz);
		this.name = name;
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		this.cw.visit(V16, ACC_PUBLIC + ACC_FINAL, name, null, Type.getInternalName(Object.class), null);
	}

	public void createConstructor() {
		try (MethodHandler mh = MethodHandler.createVoid(this, this.io, ACC_PUBLIC, "<init>")) {
			mh.visitIntInsn(ALOAD, 0);
			mh.callSpecialMethod(Object.class, "<init>", Void.TYPE);
			mh.returnOp();
		}
	}

	public void createEncode(TypeInfo info, MethodMetadata methodMetadata) {
		try (MethodHandler mh = MethodHandler.createVoid(
				this,
				this.io,
				ACC_PUBLIC | ACC_STATIC | ACC_FINAL,
				Constants.PUT_FUNC + info.getMethodName(false),
				this.io.ioClass,
				info.getClazz())) {

			var io = mh.createVar("io", this.io.ioClass);
			var data = mh.createVar("data", info.getClazz());

			methodMetadata.writePut(mh, io, data);
		}
	}

	public void createDecode(TypeInfo info, MethodMetadata methodMetadata) {
		try (MethodHandler mh = MethodHandler.create(
				this,
				this.io,
				ACC_PUBLIC | ACC_STATIC | ACC_FINAL,
				Constants.GET_FUNC + info.getMethodName(false),
				info.getClazz(),
				this.io.ioClass)) {
			var io = mh.createVar("io", this.io.ioClass);

			methodMetadata.writeGet(mh, io);
		}
	}

	private void createTest() {
		try (MethodHandler mh = MethodHandler.create(this, this.io, ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "test", UWUWU.class)) {
			IndyUtil.createMethodRef(
					mh,

					// target: UWUWU.uwu
					UWUWU.class,
					"uwu",
					GenUtil.getVoidMethodDesc(ArrayIO.class, Integer.class),

					// source: UwU.encodeshit
					false,
					"UwU",
					"encodeshit",

					// res
					GenUtil.getVoidMethodDesc(ArrayIO.class, Integer.class)
					// captured locals
			);

			mh.returnOp();
		}
	}

	private void createTest2() {
		try (MethodHandler mh = MethodHandler.create(this, this.io, ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "test2", UWUWU2.class, ArrayIO.class)) {
			var io = mh.createVar("io", this.io.ioClass);

			io.load();

			IndyUtil.createMethodRef(
					mh,

					// target: UWUWU.uwu
					UWUWU2.class,
					"uwu",
					GenUtil.getVoidMethodDesc(ArrayIO.class, Integer.class),

					// source: UwU.encodeshit
					false,
					"UwU",
					"encodeshit",

					// res
					GenUtil.getVoidMethodDesc(Integer.class),
					// captured locals
					ArrayIO.class
			);

			mh.returnOp();
		}
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

	public static void main(String[] args) throws Exception {
		CodegenHandler uwu = new CodegenHandler(ArrayIO.class, "UwU");

		uwu.createConstructor();

		uwu.createIntCombine();

		byte[] bytes = uwu.cw.toByteArray();

		Files.write(Path.of("./UwU.class"), bytes);
		PrintWriter pw = new PrintWriter(System.out);
		CheckClassAdapter.verify(new ClassReader(bytes), null, true, pw);

		Loader loader = new Loader(Thread.currentThread().getContextClassLoader());
		Class<?> clazz = loader.define("UwU", bytes);


		ArrayIO arrayIO = ArrayIO.create(16);

		UWUWU test = (UWUWU) clazz.getMethod("test").invoke(null);
		UWUWU2 test2 = (UWUWU2) clazz.getMethod("test2", ArrayIO.class).invoke(null, arrayIO);

		test.uwu(arrayIO, 420_69);
		test2.uwu(69_420);
		arrayIO.rewind();
		System.out.println(arrayIO.getInt());
		System.out.println(arrayIO.getInt());
	}

	public byte[] byteArray() {
		return this.cw.toByteArray();
	}

	private static class Loader extends ClassLoader {
		public Loader(ClassLoader parent) {
			super(parent);
		}

		public Class<?> define(String name, byte[] bytes) {
			return super.defineClass(name, bytes, 0, bytes.length);
		}
	}

	@FunctionalInterface
	public interface UWUWU {
		void uwu(ArrayIO io, Integer i);
	}

	@FunctionalInterface
	public interface UWUWU2 {
		void uwu(Integer i);
	}

	public Class<?> export() {
		return new ClassLoader() {
			public Class<?> define(byte[] bytes, String name) {
				return super.defineClass(name, bytes, 0, bytes.length);
			}
		}.define(cw.toByteArray(), name);
	}
}
