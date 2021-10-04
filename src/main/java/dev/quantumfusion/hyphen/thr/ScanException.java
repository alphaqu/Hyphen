package dev.quantumfusion.hyphen.thr;

import dev.quantumfusion.hyphen.type.Clazz;

import java.util.ArrayList;
import java.util.List;

public class ScanException extends RuntimeException {
	public List<Clazz> parents = new ArrayList<>();

	public ScanException(String message) {
		super(message);
	}

	@Override
	public String getMessage() {
		return super.getMessage() + parents;
	}
}
