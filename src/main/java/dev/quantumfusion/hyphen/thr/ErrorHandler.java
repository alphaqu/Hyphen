package dev.quantumfusion.hyphen.thr;

import dev.quantumfusion.hyphen.scan.type.Clz;
import dev.quantumfusion.hyphen.thr.exception.ScanException;
import dev.quantumfusion.hyphen.util.java.ArrayUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ErrorHandler {
	private static final char T = '\t';
	private static final char LN = '\n';
	private static final String TT = "\t\t";
	private final StringBuilder sb = new StringBuilder();

	private static final String LEFT_SIDE = "||";
	private static final String LEFT_PADDING = "    ";
	private static final String RIGHT_SIDE = "||";

	private static final int messageLength = 120;

	public static RuntimeException fatal(Throwable e) {
		final ErrorHandler errorHandler = new ErrorHandler();
		errorHandler.error(e);
		System.out.println(errorHandler.sb);
		if (e instanceof RuntimeException re) return re;
		return new RuntimeException(e);
	}

	public void error(Throwable e) {
		final String message = e.getMessage();
		printSeparatorLine(e.getClass().getSimpleName(), "#");
		printEmpty();
		printSeparatorLine(" ==>  " + message + "  <== ", " ");

		if (e instanceof ScanException scanException) {
			printGroup("Path", ArrayUtil.map(scanException.parents.toArray(Clz[]::new), String[]::new, Object::toString), '▲');
		}

		printGroup("Stacktrace", ArrayUtil.map(e.getStackTrace(), String[]::new, StackTraceElement::toString), '-');
		printEmpty();
		printSeparatorLine(getErrorTag(), "#");
	}

	private void printGroup(String title, String[] entries, char prefix) {
		printEmpty();
		printLine(title);
		for (String entry : entries) {
			printLine(" " + prefix + " " + entry);
		}
	}

	private void printLine(String content) {
		int paddingSize = messageLength - (LEFT_SIDE.length() + LEFT_PADDING.length() + content.length() + RIGHT_SIDE.length());
		sb.append(LEFT_SIDE).append(LEFT_PADDING).append(content);
		if (paddingSize >= 0) {
			sb.append(" ".repeat(paddingSize));
			sb.append(RIGHT_SIDE);
		}
		sb.append(LN);
	}

	private void printEmpty() {
		sb.append(LEFT_SIDE);
		sb.append(" ".repeat(messageLength - (LEFT_SIDE.length() + RIGHT_SIDE.length())));
		sb.append(RIGHT_SIDE);
		sb.append(LN);
	}

	private void printSeparatorLine(String content, String padding) {
		final int length = content.length();
		final boolean empty = length == 0;
		int paddingSize = messageLength - (LEFT_SIDE.length() + length + RIGHT_SIDE.length() + (empty ? 0 : 2));
		sb.append(LEFT_SIDE);
		if (paddingSize >= 0) {
			final int div2 = paddingSize / 2;
			sb.append(padding.repeat(div2));
			if (!empty) {
				sb.append(' ').append(content).append(' ');
			}
			sb.append(padding.repeat(paddingSize - div2));
			sb.append(RIGHT_SIDE);
		}
		sb.append(LN);
	}

	private String getErrorTag() {
		try (InputStream in = new URL("https://quantumfusion.dev/error.txt").openStream()) {
			var split = new String(in.readAllBytes(), StandardCharsets.UTF_8).split("\n");
			var random = new Random();

			for (int l = split.length, i = random.nextInt(l); l > 0; l--) {
				var trim = split[i].trim();
				if (trim.isEmpty() || trim.startsWith("//")) {
					split[i] = split[l - 1];
					continue;
				}
				return split[i];
			}
			return "could not find things";
		} catch (IOException ignored) {
		}

		return "Haha funny offline cring";
	}
}
