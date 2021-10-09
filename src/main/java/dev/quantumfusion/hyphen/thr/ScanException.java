package dev.quantumfusion.hyphen.thr;

import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.scan.type.Clz;

import java.util.ArrayList;
import java.util.List;

public class ScanException extends RuntimeException {
	public List<Clz> parents = new ArrayList<>();

	public ScanException(String message) {
		super(message);
	}

	private ScanException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessage() {
		return super.getMessage() + parents;
	}

	public ScanException addParent(Clz clz) {
		this.parents.add(clz);
		return this;
	}

	public static ScanException handle(Throwable e, Clz source) {
		if (e instanceof ScanException scanException)
			return scanException.addParent(source);

		return new ScanException(e).addParent(source);
	}

	public static void one() {
		final Clz source = Clazz.createRawClazz(int.class);
		try {
			two();
		} catch (Throwable e) {
			throw ScanException.handle(e, source);
		}
	}

	public static void two() {
		throw new ScanException("thing");
	}

}
