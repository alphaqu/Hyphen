package dev.quantumfusion.hyphen.thr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HypenException extends RuntimeException {
	private final List<ThrowEntry> entries = new ArrayList<>();

	public HypenException(String message) {
		super(message);
	}

	public HypenException(Throwable cause) {
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
		builder.append("Detail: ");
		for (ThrowHandler.Throwable throwable$ : this.entries) {
			builder.append('\n');
			for (ThrowEntry entry : throwable$.getEntries()) {
				builder.append(entry);
			}
		}
		builder.append('\n');
		builder.append('\n');
		builder.append("Stacktrace: ");
		return builder.toString();
	}

	public HypenException addEntry(ThrowHandler.Throwable throwable) {
		if (throwable instanceof ThrowEntry entry) {
			this.entries.add(0, entry);
		} else {
			this.entries.addAll(0, Arrays.asList(throwable.getEntries()));
		}
		return this;
	}

	public HypenException addEntries(ThrowHandler.Throwable... throwables) {
		if (!this.entries.isEmpty()) entries.add(0,ThrowEntry.newLine());

		for (ThrowHandler.Throwable throwable : throwables) {
			if (throwable instanceof ThrowEntry entry) entries.add(0, entry);
			else for (ThrowEntry entry : throwable.getEntries()) entries.add(0, entry);
		}
		return this;
	}
}


