package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.io.ArrayIO;
import dev.quantumfusion.hyphen.util.GenUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.objectweb.asm.Opcodes.*;

public class CodegenHandler {
	private final IOHandler io;
	final String name;
	final ClassWriter cw;

	public CodegenHandler(IOHandler io, String name) {
		this.io = io;
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
				"encode_" + info.getMethodName(false),
				this.io.ioClass,
				info.clazz)) {

			var io = mh.createVar("io", this.io.ioClass);
			var data = mh.createVar("data", info.clazz);

			methodMetadata.writePut(mh, io, data);
		}
	}

	public void createDecode(TypeInfo info, MethodMetadata methodMetadata) {
		try (MethodHandler mh = MethodHandler.create(
				this,
				this.io,
				ACC_PUBLIC | ACC_STATIC | ACC_FINAL,
				"decode_" + info.getMethodName(false),
				info.clazz,
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

	private static final boolean CRY = false;

	private void createCry(int count) {
		try (MethodHandler mh = MethodHandler.create(this, this.io, ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "cry", int.class, int.class)) {
			var input = mh.createVar("input", int.class);


			Label LSwitch = new Label();
			Label LA = new Label();
			Label LDef = new Label();


			Label[] labels = new Label[count];
			for (int i = 0; i < count; i++) {
				labels[i] = new Label();
			}


			input.load();
			mh.visitJumpInsn(IFGE, LSwitch);
			input.load();
			mh.visitInsn(INEG);
			mh.callInternalStaticMethod("cry", int.class, int.class);
			mh.visitJumpInsn(IFGE, LA);
			input.load();
			mh.visitInsn(INEG);
			mh.visitLdcInsn(20);
			mh.visitInsn(IADD);
			mh.callInternalStaticMethod("cry", int.class, int.class);
			mh.visitInsn(POP);

			mh.visitJumpInsn(GOTO, CRY ? labels[count - 1] : LSwitch);

			mh.visitLabel(LA);
			input.load();
			mh.callStaticMethod(Integer.class, "valueOf", Integer.class, int.class);
			mh.visitInsn(POP);

			mh.visitLabel(LSwitch);
			input.load();
			mh.visitTableSwitchInsn(0, count - 1, LDef, labels);

			for (int i = count - 1; i >= 0; i--) {
				mh.visitLabel(labels[i]);
				mh.visitFieldInsn(GETSTATIC, Type.getInternalName(System.class), "out", Type.getDescriptor(System.out.getClass()));
				mh.visitLdcInsn("" + i);
				mh.callInstanceMethod(PrintStream.class, "println", null, String.class);
			}

			mh.visitLabel(LDef);
			mh.visitLdcInsn(69_420);
			mh.returnOp();
		}
	}


	public static void otherMain(String[] args) throws Exception {
		CodegenHandler uwu = new CodegenHandler(IOHandler.ARRAY, "UwU");

		uwu.createConstructor();
		uwu.createEncode(null, null);
		uwu.createDecode(null, null);
		uwu.createTest();
		uwu.createTest2();

		byte[] bytes = uwu.cw.toByteArray();

		Files.write(Path.of("./uwu.class"), bytes);

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


	public static void main(String[] args) throws Exception {
		CodegenHandler uwu = new CodegenHandler(IOHandler.ARRAY, "CRY");

		uwu.createCry(100);

		byte[] bytes = uwu.cw.toByteArray();

		Files.write(Path.of("./cry.class"), bytes);
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
