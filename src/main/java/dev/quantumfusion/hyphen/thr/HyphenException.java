package dev.quantumfusion.hyphen.thr;

import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.scan.type.ParaClazz;
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
		this(cause.getMessage(), cause, possibleSolution);
	}

	public HyphenException(String message, Throwable cause, @Nullable String possibleSolution) {
		super(message, cause);
		this.setStackTrace(cause.getStackTrace());
		this.possibleSolution = possibleSolution;
	}

	public static HyphenException rethrow(Clazz aClass, @Nullable String entry, Throwable throwable) {
		if (throwable instanceof HyphenException exception) {
			return exception.rethrow(aClass, entry);
		}
		var exception = new HyphenException(throwable, null);
		exception.rethrow(aClass, entry);
		return exception;
	}

	public HyphenException rethrow(Clazz aClass, @Nullable String entry) {
		this.path.add(new Entry(aClass, entry));
		return this;
	}

	@Override
	public void printStackTrace(PrintStream s) {
		super.printStackTrace(s);
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		super.printStackTrace(s);
	}
	public record Entry(Clazz aClass, @Nullable String entry) {

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getName());
		builder.append(": \n");

		builder.append("Cause: ");
		String message = this.getMessage();
		builder.append(message);

		if (possibleSolution != null) {
			builder.append("Suggestion: ");
			builder.append(this.possibleSolution);
			builder.append("\n");
		}
		builder.append("\n");
		builder.append("Object Stacktrace:");
		Clazz lastClass = null;
		for (Entry entry : this.path) {
			if (lastClass != entry.aClass) {
				lastClass = entry.aClass;
			} else if (entry.entry == null) {
				continue;
			}
			builder.append("\n\t");
			if (entry.entry != null) {
				builder.append("at ");
				builder.append(entry.entry);
				builder.append(" ");
			}
			builder.append("in ");
			builder.append(entry.aClass);

		}
		builder.append("\n\n");
		builder.append("Stacktrace:");

		return builder.toString();
	}
}
