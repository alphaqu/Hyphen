package dev.quantumfusion.hyphen.thr.exception;

public class IncompatibleTypeException extends HyphenException {

	public IncompatibleTypeException(String message) {
		super(message);
	}

	private IncompatibleTypeException(Throwable cause) {
		super(cause);
	}
}
