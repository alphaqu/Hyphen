package dev.quantumfusion.hyphen.thr.exception;

public class UnknownTypeException extends HyphenException {

	public UnknownTypeException(String message) {
		super(message);
	}

	private UnknownTypeException(Throwable cause) {
		super(cause);
	}
}
