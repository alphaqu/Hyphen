package dev.quantumfusion.hyphen.thr.exception;

import dev.quantumfusion.hyphen.scan.type.Clazz;
import dev.quantumfusion.hyphen.scan.type.Clz;

import java.util.ArrayList;
import java.util.List;

public class HyphenException extends RuntimeException {
	public List<Clz> parents = new ArrayList<>();

	public HyphenException(String message) {
		super(message);
	}

	protected HyphenException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessage() {
		return super.getMessage() + parents;
	}

	public HyphenException addParent(Clz clz) {
		this.parents.add(clz);
		return this;
	}

	public static HyphenException handle(Throwable e, Clz source) {
		if (e instanceof HyphenException scanException)
			return scanException.addParent(source);

		return new HyphenException(e).addParent(source);
	}

	public static void one() {
		final Clz source = Clazz.createRawClazz(int.class);
		try {
			two();
		} catch (Throwable e) {
			throw HyphenException.handle(e, source);
		}
	}

	public static void two() {
		throw new HyphenException("thing");
	}

}
