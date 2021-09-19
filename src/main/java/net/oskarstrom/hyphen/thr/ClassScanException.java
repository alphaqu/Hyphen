package net.oskarstrom.hyphen.thr;

public class ClassScanException extends HypenException {
	public ClassScanException() {
	}

	public ClassScanException(String message) {
		super(message);
	}

	public ClassScanException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClassScanException(Throwable cause) {
		super(cause);
	}

	public ClassScanException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
