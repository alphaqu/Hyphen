package net.oskarstrom.hyphen.thr;

public class MissingTypeInformationException extends HypenException {

	public MissingTypeInformationException() {
	}

	public MissingTypeInformationException(String message) {
		super(message);
	}

	public MissingTypeInformationException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingTypeInformationException(Throwable cause) {
		super(cause);
	}

	public MissingTypeInformationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}


