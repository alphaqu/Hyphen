package net.oskarstrom.hyphen.thr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HypenException extends RuntimeException {
	List<ThrowHandler.ThrowEntry> entries = new ArrayList<>();

	public HypenException() {
	}

	public HypenException(String message) {
		super(message);
	}

	public HypenException(String message, Throwable cause) {
		super(message, cause);
	}

	public HypenException(Throwable cause) {
		super(cause);
	}

	public HypenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
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
			for (ThrowHandler.ThrowEntry entry : throwable$.getEntries()) {
				builder.append(entry);
			}
		}
		builder.append('\n');
		builder.append('\n');
		builder.append("Stacktrace: ");
		return builder.toString();
	}

	public HypenException addEntry(ThrowHandler.Throwable throwable){
		if(throwable instanceof ThrowHandler.ThrowEntry entry) {
			this.entries.add(0,entry);
		} else {
			this.entries.addAll(0, Arrays.asList(throwable.getEntries()));
		}
		return this;
	}

	public HypenException addEntries(ThrowHandler.Throwable ... throwables) {
		List<ThrowHandler.ThrowEntry> newEntries = new ArrayList<>();
		for (ThrowHandler.Throwable throwable : throwables) {
			if(throwable instanceof ThrowHandler.ThrowEntry entry) {
				newEntries.add(entry);
			} else {
				newEntries.addAll( Arrays.asList(throwable.getEntries()));
			}
		}
		if(!this.entries.isEmpty()) {
			newEntries.add(ThrowHandler.ThrowEntry.newLine());
			newEntries.addAll(this.entries);
		}
		this.entries = newEntries;
		return this;
	}
}


