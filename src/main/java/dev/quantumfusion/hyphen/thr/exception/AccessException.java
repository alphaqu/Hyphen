package dev.quantumfusion.hyphen.thr.exception;

public class AccessException extends HyphenException {

	public AccessException(String message) {
		super(message);
	}

	private AccessException(Throwable cause) {
		super(cause);
	}
}
