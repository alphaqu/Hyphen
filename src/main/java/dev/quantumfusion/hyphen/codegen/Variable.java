package dev.quantumfusion.hyphen.codegen;

import org.objectweb.asm.Type;

public record Variable(int id, String name, Class<?> clazz) {

	public int op(int op) {
		return Type.getType(clazz).getOpcode(op);
	}

	public enum Standard {
		IO,
		DATA
	}
}
