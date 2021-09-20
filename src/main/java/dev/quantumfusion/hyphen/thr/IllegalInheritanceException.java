package dev.quantumfusion.hyphen.thr;

public class IllegalInheritanceException extends HypenException {

	public IllegalInheritanceException() {
	}

	public IllegalInheritanceException(String message) {
		super(message);
	}

	public IllegalInheritanceException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalInheritanceException(Throwable cause) {
		super(cause);
	}

	public IllegalInheritanceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
