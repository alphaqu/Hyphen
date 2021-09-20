package dev.quantumfusion.hyphen.util;

import org.jetbrains.annotations.NotNull;

public enum Color implements CharSequence {
	RESET("\u001B[0m"),
	BLACK("\u001B[30m"),
	RED("\u001B[31m"),
	GREEN("\u001B[32m"),
	YELLOW("\u001B[33m"),
	BLUE("\u001B[34m"),
	PURPLE("\u001B[35m"),
	CYAN("\u001B[36m"),
	WHITE("\u001B[37m");

	String code;

	Color(String code) {
		this.code = code;
	}

	@Override
	public int length() {
		return code.length();
	}

	@Override
	public char charAt(int index) {
		return code.charAt(index);
	}

	@NotNull
	@Override
	public CharSequence subSequence(int start, int end) {
		return code.subSequence(start, end);
	}

	@Override
	public String toString() {
		return code;
	}
}
