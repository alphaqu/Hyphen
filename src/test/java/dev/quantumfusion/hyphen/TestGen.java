package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.annotations.Data;

public class TestGen {

	public static void main(String[] args) {

	}

	public static class Test {
		@Data
		public int anInt;

		public Test(int anInt) {
			this.anInt = anInt;
		}
	}
}
