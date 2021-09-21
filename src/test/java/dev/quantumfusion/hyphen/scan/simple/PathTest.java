package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.annotation.Serialize;

@

public class PathTest {
	@Serialize
	public Thing1 superGaming;

	public PathTest(Thing1 superGaming) {
		this.superGaming = superGaming;
	}


	public static class Thing1 {

		@Serialize
		public Thing2 rice;

		public Thing1(Thing2 rice) {
			this.rice = rice;
		}

		public static class Thing2 {

			@Serialize
			public Thing3 model;

			public Thing2(Thing3 model) {
				this.model = model;
			}

			public static class Thing3 {

				@Serialize
				public Thing4 block;

				public Thing3(Thing4 block) {
					this.block = block;
				}

				public static class Thing4 {

					@Serialize
					public Thing2 thing1;

				}
			}
		}
	}
}
