package dev.quantumfusion.hyphen.thr.exception;

import dev.quantumfusion.hyphen.info.TypeInfo;
import dev.quantumfusion.hyphen.thr.ThrowEntry;
import dev.quantumfusion.hyphen.thr.ThrowHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HyphenException extends RuntimeException {
	private final List<ThrowEntry> entries = new ArrayList<>();
	private final List<PathEntry> path = new ArrayList<>();

	public HyphenException(String message) {
		super(message);
	}

	public HyphenException(java.lang.Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append('\n');
		builder.append('\n');
		builder.append("Reason: ");
		builder.append("\n\t");
		builder.append(super.getMessage());
		builder.append('\n');
		builder.append('\n');

		if (!this.path.isEmpty()) {
			builder.append("Path: ");
			for (PathEntry typeInfo : this.path) {
				builder.append('\n');
				builder.append("\t- field \"");
				if (typeInfo == null) builder.append("null");
				else {
					builder.append(typeInfo.name);
					builder.append("\" in class \"");
					builder.append(typeInfo.parent.clazz.getSimpleName());
					builder.append('\"');
				}
			}
			builder.append('\n');
			builder.append('\n');
		}

		if (!this.entries.isEmpty()) {
			builder.append("Detail: ");

			for (ThrowHandler.Throwable throwable$ : this.entries) {
				builder.append("\n");
				for (ThrowEntry entry : throwable$.getEntries()) {
					builder.append(entry);
				}
			}

			builder.append('\n');
			builder.append('\n');
		}

		builder.append("Stacktrace: ");
		return builder.toString();
	}

	public HyphenException addEntry(ThrowHandler.Throwable throwable) {
		if (throwable instanceof ThrowEntry entry) {
			this.entries.add(0, entry);
		} else {
			this.entries.addAll(0, Arrays.asList(throwable.getEntries()));
		}
		return this;
	}

	public HyphenException addParent(TypeInfo parent, String name) {
		this.path.add(0, new PathEntry(parent, name));
		return this;
	}

	public HyphenException addEntries(ThrowHandler.Throwable... throwables) {
		if (!this.entries.isEmpty()) entries.add(0, ThrowEntry.newLine());

		for (int i = throwables.length - 1; i >= 0; i--) {
			var throwable = throwables[i];
			if (throwable instanceof ThrowEntry entry) entries.add(0, entry);
			else for (ThrowEntry entry : throwable.getEntries()) entries.add(0, entry);
		}
		return this;
	}

	private record PathEntry(TypeInfo parent, String name) {
	}
}


