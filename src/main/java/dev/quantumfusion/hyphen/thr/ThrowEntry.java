package dev.quantumfusion.hyphen.thr;

public class ThrowEntry implements ThrowHandler.Throwable {
	private final String key;
	private final String value;

	public ThrowEntry(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public static ThrowEntry of(String key, Object value) {
		return of(key, value == null ? null : value.getClass().getSimpleName() + ": " + value);
	}

	public static ThrowEntry of(String key, String value) {
		return new ThrowEntry(key, value);
	}

	public static ThrowEntry newLine() {
		return new ThrowEntry(null, null) {
			@Override
			public String toString() {
				return "";
			}
		};
	}


	@Override
	public ThrowEntry[] getEntries() {
		return new ThrowEntry[]{this};
	}

	@Override
	public String toString() {
		return "\t- " + this.key + ":  " + this.value;
	}
}
