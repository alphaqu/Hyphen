package dev.quantumfusion.hyphen.thr.exception;

public class IllegalInheritanceException extends HyphenException {

	public IllegalInheritanceException(String message) {
		super(message);
	}

	private IllegalInheritanceException(Throwable cause) {
		super(cause);
	}
}
