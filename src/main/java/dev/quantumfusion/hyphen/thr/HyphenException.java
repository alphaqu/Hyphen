package dev.quantumfusion.hyphen.thr;

import dev.quantumfusion.hyphen.util.Style;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class HyphenException extends RuntimeException {
	private final List<Entry> path = new ArrayList<>();
	@Nullable
	private final String possibleSolution;

	public HyphenException(Throwable cause, @Nullable String possibleSolution) {
		super(cause.getMessage(), cause);
		setStackTrace(cause.getStackTrace());
		this.possibleSolution = possibleSolution;
	}

	public static HyphenException thr(String type, String separator, Object clazz, Throwable throwable) {
		if (throwable instanceof HyphenException exception) return exception.append(type, separator, clazz);
		var exception = new HyphenException(throwable, null);
		exception.append(type, separator, clazz);
		return exception;
	}

	public HyphenException append(String type, String separator, Object clazz) {
		path.add(new Entry(type, separator, clazz));
		return this;
	}

	private static void printTitle(StringBuilder sb, String name) {
		sb.append('\t').append(Style.GREEN).append(name).append(Style.RESET).append('\n');
	}

	private static void printEntry(String separator, StringBuilder sb, Entry test) {
		printEntry(separator, sb, test, false);
	}

	private static void printEntry(String separator, StringBuilder sb, Entry test, boolean error) {
		String backgroundStyle;
		String separatorStyle;
		if (error) {
			backgroundStyle = Style.RED_BACKGROUND + Style.BLACK;
			separatorStyle = Style.BLACK;
		} else {
			backgroundStyle = Style.RESET;
			separatorStyle = Style.PURPLE;
		}

		sb.append("\t\t");
		sb.append(backgroundStyle).append(' ').append(separatorStyle).append(separator);
		sb.append(backgroundStyle).append(test.type);
		sb.append(backgroundStyle).append(' ').append(separatorStyle).append(test.separator).append(' ');
		sb.append(backgroundStyle).append(test.object);
		sb.append(Style.RESET).append('\n');
	}

	@Override
	public void printStackTrace(PrintStream s) {
		s.println(niceException());
	}

	public String niceException() {
		StringBuilder sb = new StringBuilder(Style.YELLOW + "" + "\n\t <$ Hyphen Fatal Exception $> " + Style.RESET);
		sb.append('\n');
		sb.append('\n');
		printTitle(sb, "Exception");
		printEntry(" - ", sb, new Entry("reason", "|>", getMessage()), true);
		printEntry(" - ", sb, new Entry("reason", "|>", getCause().getClass().getSimpleName()));
		sb.append('\n');

		if (possibleSolution != null) {
			printTitle(sb, "Possible solution");
			printEntry(" #! ", sb, new Entry("suggestion", Style.LINE_DOWN, possibleSolution));
			sb.append('\n');
		}

		printTitle(sb, "Path");
		boolean first = true;
		for (Entry clazz : path) {
			if (first) {
				printEntry(" -> ", sb, clazz, true);
			} else {
				printEntry(" ↑  ", sb, clazz);
			}
			first = false;
		}
		sb.append('\n');

		printTitle(sb, "Stacktrace");
		first = true;
		for (var element : getStackTrace()) {
			printEntry(" -> ", sb, Entry.create(element), first);
			first = false;
		}
		sb.append('\n');

		return sb.toString();
	}

	public record Entry(String type, String separator, Object object) {
		public static Entry create(StackTraceElement object) {
			return new Entry((object.isNativeMethod() ? "native" : "java") + ":" + object.getLineNumber(), Style.LINE_DOWN, object);
		}
	}

}
