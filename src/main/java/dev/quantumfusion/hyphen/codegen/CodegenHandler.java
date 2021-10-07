package dev.quantumfusion.hyphen.codegen;

import org.objectweb.asm.ClassWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;

public class CodegenHandler extends ClassWriter {
	private final Map<MethodInfo, AtomicInteger> methodDeduplication = new HashMap<>();
	private final List<SerializerMethodDef> methods;
	private final boolean debug;
	public final String className;

	public CodegenHandler(String className, boolean debug, List<SerializerMethodDef> methods) {
		super(null, ClassWriter.COMPUTE_FRAMES);
		this.visit(V16, ACC_FINAL | ACC_PUBLIC, className, null, null, null);
		this.methods = methods;
		this.debug = debug;
		this.className = className;
	}

	public void createMethods() {
		methods.forEach(def -> createMethod(def.getInfo, def::writeGetMethod));
		methods.forEach(def -> createMethod(def.putInfo, def::writePutMethod));
		methods.forEach(def -> createMethod(def.measureInfo, def::writeMeasureMethod));
	}

	public MethodInfo apply(MethodInfo source) {
		if(debug) return source;
		var methodId = methodDeduplication.computeIfAbsent(source, info -> new AtomicInteger(0));
		var id = methodId.getAndIncrement();
		return new MethodInfo(String.valueOf(id), source.returnClass, source.parameters);
	}

	private void createMethod(MethodInfo info, Consumer<MethodHandler> func) {
		try (var mh = new MethodHandler(this, info)) {
			func.accept(mh);
		}
	}

	public static void main(String[] args) {
		final CodegenHandler f = new CodegenHandler("f", false, new ArrayList<>());
		try (var mh = new MethodHandler(f, "thing", Object.class)) {

		}
	}
}
