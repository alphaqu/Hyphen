package dev.quantumfusion.hyphen.thr.exception;

public class ScanException extends HyphenException {

	public ScanException(String message) {
		super(message);
	}

	private ScanException(Throwable cause) {
		super(cause);
	}
}
