
package net.oskarstrom.hyphen.thr;

import java.util.function.Function;

public class ThrowHandler {

	//dollar for the styles.
	//returns just for javac to stfu in some spots
	public static RuntimeException fatal(Function<String, RuntimeException> ex, String reason, Throwable... throwable) {
		StringBuilder builder = new StringBuilder();
		builder.append('\n');
		builder.append('\n');
		builder.append("Reason: ");
		builder.append("\n\t");
		builder.append(reason);
		builder.append('\n');
		builder.append('\n');
		builder.append("Detail: ");
		for (Throwable throwable$ : throwable) {
			builder.append('\n');
			for (ThrowEntry entry : throwable$.getEntries()) {
				builder.append(entry);
			}
		}
		builder.append('\n');
		builder.append('\n');
		builder.append("Stacktrace: ");
		throw ex.apply(builder.toString());
	}

	public interface Throwable {
		ThrowEntry[] getEntries();
	}

	public static class ThrowEntry implements Throwable {
		public final String key;
		public final String value;

		public ThrowEntry(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public static ThrowEntry of(String key, String value) {
			return new ThrowEntry(key, value);
		}

		@Override
		public ThrowEntry[] getEntries() {
			return new ThrowEntry[]{this};
		}

		@Override
		public String toString() {
			return "\t" + key + ": " + value;
		}
	}

}
