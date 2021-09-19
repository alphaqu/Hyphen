package net.oskarstrom.hyphen.thr;

public class IllegalInheritanceException extends RuntimeException {

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
