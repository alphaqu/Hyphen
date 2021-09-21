package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.thr.AccessException;

@FailTest(AccessException.class)
public class PathTest {
	@Serialize
	public Thing1 superGaming;

	public PathTest(Thing1 superGaming) {
		this.superGaming = superGaming;
	}


	// FIXME: the tester shouldn't run inner classes
	@FailTest(AccessException.class)
	public static class Thing1 {

		@Serialize
		public Thing2 rice;

		public Thing1(Thing2 rice) {
			this.rice = rice;
		}

		// FIXME: the tester shouldn't run inner classes
		@FailTest(AccessException.class)
		public static class Thing2 {

			@Serialize
			public Thing3 model;

			public Thing2(Thing3 model) {
				this.model = model;
			}

			// FIXME: the tester shouldn't run inner classes
			@FailTest(AccessException.class)
			public static class Thing3 {

				@Serialize
				public Thing4 block;

				public Thing3(Thing4 block) {
					this.block = block;
				}

				// FIXME: the tester shouldn't run inner classes
				@FailTest(AccessException.class)
				public static class Thing4 {

					@Serialize
					public Thing2 thing1;

				}
			}
		}
	}
}
