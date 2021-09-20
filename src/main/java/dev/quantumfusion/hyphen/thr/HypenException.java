package dev.quantumfusion.hyphen.thr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HypenException extends RuntimeException {
	List<ThrowEntry> entries = new ArrayList<>();

	public HypenException(String message) {
		super(message);
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

	public HypenException addEntry(ThrowHandler.Throwable throwable){
		if(throwable instanceof ThrowEntry entry) {
			this.entries.add(0,entry);
		} else {
			this.entries.addAll(0, Arrays.asList(throwable.getEntries()));
		}
		return this;
	}

	public HypenException addEntries(ThrowHandler.Throwable ... throwables) {
		List<ThrowEntry> newEntries = new ArrayList<>();
		for (ThrowHandler.Throwable throwable : throwables) {
			if(throwable instanceof ThrowEntry entry) {
				newEntries.add(entry);
			} else {
				newEntries.addAll( Arrays.asList(throwable.getEntries()));
			}
		}
		if(!this.entries.isEmpty()) {
			newEntries.add(ThrowEntry.newLine());
			newEntries.addAll(this.entries);
		}
		this.entries = newEntries;
		return this;
	}
}


