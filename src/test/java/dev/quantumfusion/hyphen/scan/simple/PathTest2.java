package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.thr.exception.AccessException;

import java.util.Iterator;

@FailTest(AccessException.class)
public class PathTest2 {
	@Serialize
	public Thing1[] superGaming;

	public PathTest2(Thing1[] superGaming) {
		this.superGaming = superGaming;
	}

	@FailTest(AccessException.class)
	public static class Thing1 {

		@Serialize
		public Thing2[][] rice;

		public Thing1(Thing2[][] rice) {
			this.rice = rice;
		}

		@FailTest(AccessException.class)
		public static class Thing2 {

			@Serialize
			public Thing3[][][] model;

			public Thing2(Thing3[][][] model) {
				this.model = model;
			}

			@FailTest(AccessException.class)
			public static class Thing3 {

				@Serialize
				public Thing4[][][][] block;

				public Thing3(Thing4[][][][] block) {
					this.block = block;
				}

				@FailTest(AccessException.class)
				public static class Thing4 {

					@Serialize
					public Iterator list;

					public Thing4(Iterator list) {
						this.list = list;
					}
				}
			}
		}
	}
}
