package dev.quantumfusion.hyphen.thr;

import org.jetbrains.annotations.Nullable;

public class UnknownTypeException extends HyphenException {
	public UnknownTypeException(String message, @Nullable String possibleFix) {
		super(message, possibleFix);
	}
}
