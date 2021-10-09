package dev.quantumfusion.hyphen.thr;

import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.scan.type.Clz;
import dev.quantumfusion.hyphen.util.ArrayUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.List;
import java.util.Random;

public class ErrorHandler {
	private static final char T = '\t';
	private static final char LN = '\n';
	private static final String TT = String.valueOf(T + T);
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
		try {
			URL url = new URL("https://quantumfusion.dev/error.txt");
			final URLConnection urlConnection = url.openConnection();
			final byte[] bytes = urlConnection.getInputStream().readAllBytes();
			final String[] split = new String(bytes).split("\n");

			final Random random = new Random();
			for (int i = 0; i < 5000; i++) {
				final String s = split[random.nextInt(split.length)];
				final String trim = s.trim();
				if (trim.isEmpty() || trim.startsWith("//"))
					continue;
				return s;
			}
			return "could not find things";
		} catch (IOException ignored) {
		}
		return "Haha funny offline cring";
	}
}
