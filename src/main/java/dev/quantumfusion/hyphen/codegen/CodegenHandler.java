package dev.quantumfusion.hyphen.codegen;

import dev.quantumfusion.hyphen.HyphenSerializer;
import dev.quantumfusion.hyphen.codegen.method.MethodMetadata;
import dev.quantumfusion.hyphen.info.TypeInfo;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.util.Map;

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
		try (MethodHandler mh = MethodHandler.createVoid(this, ACC_PUBLIC, "<init>")) {
			mh.visitIntInsn(ALOAD, 0);
			mh.callSpecialMethod(Object.class, "<init>", Void.TYPE);
			mh.returnOp();
		}
	}

	public void createMethods(Map<TypeInfo, MethodMetadata> methods) {
		methods.values().forEach(methodMetadata -> methodMetadata.createPut(this));
		methods.values().forEach(methodMetadata -> methodMetadata.createGet(this));
		methods.values().forEach(methodMetadata -> methodMetadata.createSubCalc(this));
	}

	public void createMainMethods(MethodMetadata mainSerializeMethod) {
		this.createMainEncode(mainSerializeMethod);
		this.createMainDecode(mainSerializeMethod);
		this.createMainSize(mainSerializeMethod);
	}

	private void createMainEncode(MethodMetadata methodMetadata) {
		try (MethodHandler mh = MethodHandler.createVoid(
				this,
				ACC_PUBLIC | ACC_FINAL,
				"encode",
				Object.class,
				Object.class)
		) {

			mh.createVar("this", Object.class);
			var ioRaw = mh.createVar("ioRaw", Object.class);
			var dataRaw = mh.createVar("dataRaw", Object.class);
			var io = mh.createVar("io", this.io.ioClass);
			var data = mh.createVar("data", methodMetadata.getInfo().getClazz());

			ioRaw.load();
			mh.cast(this.io.ioClass);
			io.store();

			dataRaw.load();
			mh.cast(methodMetadata.getInfo().getClazz());
			data.store();
			// TODO should this call the static put instead?
			//  methodMetadata.callPut(mh);
			methodMetadata.writePut(mh, io, data);
		}
	}

	private void createMainDecode(MethodMetadata methodMetadata) {
		try (MethodHandler mh = MethodHandler.create(
				this,
				ACC_PUBLIC | ACC_FINAL,
				"decode",
				Object.class,
				Object.class)) {

			mh.createVar("this", Object.class);
			var ioRaw = mh.createVar("ioRaw", Object.class);
			var io = mh.createVar("io", this.io.ioClass);
			ioRaw.load();
			mh.cast(this.io.ioClass);
			io.store();

			// TODO should this call the static get instead?
			//  methodMetadata.callPut(mh);
			methodMetadata.writeGet(mh, io);
		}
	}

	private void createMainSize(MethodMetadata methodMetadata) {
		TypeInfo info = methodMetadata.getInfo();
		try (MethodHandler mh = MethodHandler.create(
				this,
				ACC_PUBLIC | ACC_FINAL,
				"measure",
				long.class,
				Object.class)
		) {
			long size = methodMetadata.getSize();
			if (size >= 0) {
				mh.visitLdcInsn(size);
				mh.returnOp();
			} else {
				mh.createVar("this", Object.class);
				var dataRaw = mh.createVar("dataRaw", Object.class);
				var data = mh.createVar("data", info.getClazz());
				dataRaw.load();
				mh.cast(info.getClazz());
				data.store();
				methodMetadata.writeSubCalcSize(mh, data);

				size = ~size;

				if (size != 0) {
					mh.visitLdcInsn(size);
					mh.visitInsn(LADD);
				}

				mh.returnOp();
			}
		}
	}

	public byte[] byteArray() {
		return this.cw.toByteArray();
	}

	public Class<?> export() {
		return new ClassLoader() {
			Class<?> define(byte[] bytes, String name) {
				return super.defineClass(name, bytes, 0, bytes.length);
			}
		}.define(this.cw.toByteArray(), this.name);
	}

	public IOMode getIOMode() {
		return this.io;
	}
}
