package dev.quantumfusion.hyphen.thr;

import dev.quantumfusion.hyphen.util.Style;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HyphenException extends RuntimeException {
	private final List<Entry> path = new ArrayList<>();
	@Nullable
	private final String possibleSolution;

	public HyphenException() {
		super();
		this.possibleSolution = null;
	}

	public HyphenException(String message, @Nullable String possibleSolution) {
		super(message);
		this.possibleSolution = possibleSolution;
	}

	public HyphenException(Throwable cause, @Nullable String possibleSolution) {
		super(cause.getMessage(), cause);
		this.setStackTrace(cause.getStackTrace());
		this.possibleSolution = possibleSolution;
	}

	public static HyphenException thr(String type, String separator, Object clazz, Throwable throwable) {
		if (throwable instanceof HyphenException exception) return exception.append(type, separator, clazz);
		var exception = new HyphenException(throwable, null);
		exception.append(type, separator, clazz);
		return exception;
	}

	public HyphenException append(String type, String separator, Object clazz) {
		this.path.add(new Entry(type, separator, clazz));
		return this;
	}

	@Override
	public void printStackTrace(PrintStream s) {
		s.println(this.niceException());
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		s.println(this.niceException());
	}

	public String niceException() {
		StringBuilder sb = new StringBuilder("now handled as a " + Style.YELLOW + "<$ Hyphen Fatal Exception $>" + Style.RESET + "\n\n");
		List<Entry> main = new ArrayList<>();
		main.add(new Entry(null, null, this.getMessage()));
		if (getCause() != null)
			main.add(new Entry("cause: ", null, getCause().getClass().getSimpleName()));

		new Group(this.getClass().getSimpleName() + " Reason", null, main).append(sb);

		if (possibleSolution != null)
			new Group("Possible Solution", null, Collections.singletonList(new Entry(null, null, possibleSolution))).append(sb);

		new Group("Path", Style.RED_BACKGROUND, this.path).append(sb);
		new Group("Stacktrace", Style.RED_BACKGROUND, Arrays.stream(this.getStackTrace()).map(Entry::create).collect(Collectors.toList())).append(sb);
		return sb.toString();
	}

	public record Entry(String type, String separator, Object object) {
		public static Entry create(StackTraceElement object) {
			return new Entry((object.isNativeMethod() ? "native" : "java") + ":" + object.getLineNumber(), Style.LINE_DOWN, object);
		}
	}

	public record Group(String name, String topBackground, List<Entry> entries) {
		public void append(StringBuilder sb) {
			sb.append('\t').append(Style.GREEN).append(name).append(Style.RESET).append('\n');
			boolean first = true;
			for (Entry entry : entries) {
				String path = " ↑  ";
				String style = Style.RESET;
				String accents = Style.PURPLE;
				if (first) {
					path = " -> ";
					if (topBackground != null) {
						style = Style.BLACK + topBackground;
						accents = Style.BLACK;
					}
				}
				sb.append("\t\t").append(style).append(accents).append(path);
				if (entry.type != null) {
					sb.append(style).append(entry.type);
				}
				if (entry.separator != null) {
					sb.append(accents).append(' ').append(entry.separator).append(' ');
				}
				sb.append(style).append(entry.object);
				sb.append(Style.RESET).append('\n');
				first = false;
			}

			sb.append('\n');
		}
	}

}
