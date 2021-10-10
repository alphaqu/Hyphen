package dev.quantumfusion.hyphen.thr.exception;

public class IllegalClassException extends HyphenException {

	public IllegalClassException(String message) {
		super(message);
	}

	private IllegalClassException(Throwable cause) {
		super(cause);
	}
}
