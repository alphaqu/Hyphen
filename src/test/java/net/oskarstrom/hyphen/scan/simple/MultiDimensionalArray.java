package net.oskarstrom.hyphen.scan.simple;

import net.oskarstrom.hyphen.annotation.Serialize;

public class MultiDimensionalArray {

	@Serialize
	public ObjectTest[][][][][][][][][][][][] bruh;

	public MultiDimensionalArray(ObjectTest[][][][][][][][][][][][] bruh) {
		this.bruh = bruh;
	}
}
