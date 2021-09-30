package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.io.UnsafeIO;

import java.util.Locale;

public class IODef extends SerializerDef {
	private final Class<?> clazz;

	public IODef(Class<?> clazz) {
		this.clazz = clazz;

	}

	@Override
	public Class<?> getType() {
		return clazz;
	}

	private String name(){
		String s;
		if (this.clazz.isArray()) s = this.clazz.componentType().getSimpleName() + "Array";
		else s = this.clazz.getSimpleName();
		return s.substring(0,1).toUpperCase(Locale.ROOT) + s.substring(1);
	}

	@Override
	public void doPut(MethodHandler mh) {
		mh.callStaticMethod(UnsafeIO.class, "put" + this.name(), null, this.clazz);
	}

	@Override
	public void doGet(MethodHandler mh) {
		mh.callStaticMethod(UnsafeIO.class, "get" + this.name(), this.clazz);
	}
}
