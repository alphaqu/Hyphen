package dev.quantumfusion.hyphen.thr;

import dev.quantumfusion.hyphen.scan.type.Clazz;

import java.util.ArrayList;
import java.util.List;

public class HyphenException extends RuntimeException {
	private final List<Clazz> path = new ArrayList<>();

	public HyphenException(String message) {
		super(message);
	}

	public HyphenException(Throwable cause) {
		super(cause);
	}

	public HyphenException append(Clazz clazz) {
		path.add(0, clazz);
		return this;
	}

	public static HyphenException thr(Clazz clazz, Throwable throwable) {
		if (throwable instanceof HyphenException exception) return exception.append(clazz);
		var exception = new HyphenException(throwable);
		exception.append(clazz);
		return exception;
	}
	//	Super Duper errror
	//		- NullPointerException
	//
	//	Path
	//		- thign
	//		-


	private static void printTitle(StringBuilder sb, String name) {
		sb.append('\t').append(name).append('\n');
	}

	private static void printEntry(StringBuilder sb, Object test) {
		printEntry(sb, test.toString());
	}

	private static void printEntry(StringBuilder sb, String test) {
		sb.append("\t\t").append(" - ").append(test).append('\n');
	}

	public String niceException() {
		StringBuilder sb = new StringBuilder();
		printTitle(sb, getMessage());
		printEntry(sb, getCause());
		sb.append('\n');

		printTitle(sb, "Path");
		for (var clazz : path) printEntry(sb, clazz);
		sb.append('\n');

		printTitle(sb, "Stacktrace");
		for (var element : getStackTrace()) printEntry(sb, element);
		sb.append('\n');

		return sb.toString();
	}
}
