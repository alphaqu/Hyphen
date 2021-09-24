package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;

import java.util.Arrays;
import java.util.Objects;

public class TestClass {
	@Serialize
	public int thinbruh2;


	//@Serialize
	public Component a;
	//@Serialize
	public Component[] b;
	@Serialize
	public Component[][] c;
	//@Serialize
	public Component[][][][] d;
	//@Serialize
	public Component[][][][][][][][] e;

	public TestClass(Component[][][][][][][][] e) {
		this.e = e;
	}

	@Serialize
	public int[][] primArr;
	@Serialize
	public int thinbruh3;
	@Serialize
	public long thinbruh4;
	@Serialize
	@SerSubclasses({Component0.class, Component.class})
	public Object thinbruh5;
	@Serialize
	@SerSubclasses({Component0.class, Component.class, TestClass.class})
	public Object thinbruh6;

	public TestClass(int thinbruh2, Component[][] c, int[][] primArr, int thinbruh3, long thinbruh4, Object thinbruh5, Object thinbruh6) {
		this.thinbruh2 = thinbruh2;
		this.c = c;
		this.primArr = primArr;
		this.thinbruh3 = thinbruh3;
		this.thinbruh4 = thinbruh4;
		this.thinbruh5 = thinbruh5;
		this.thinbruh6 = thinbruh6;
	}

	@Override
	public boolean equals(Object o) {
		return this == o
				|| o instanceof TestClass test
				&& this.thinbruh2 == test.thinbruh2
				&& this.thinbruh3 == test.thinbruh3
				&& this.thinbruh4 == test.thinbruh4
				&& Objects.equals(this.thinbruh5, test.thinbruh5)
				&& Objects.equals(this.thinbruh6, test.thinbruh6)
				&& Objects.equals(this.a, test.a)
				&& Arrays.deepEquals(this.b, test.b)
				&& Arrays.deepEquals(this.c, test.c)
				&& Arrays.deepEquals(this.d, test.d)
				&& Arrays.deepEquals(this.e, test.e)
				&& Arrays.deepEquals(this.primArr, test.primArr)
				;
	}

	@Override
	public int hashCode() {
		return Objects.hash(
				this.thinbruh2,
				this.thinbruh3,
				this.thinbruh4,
				this.thinbruh5,
				this.thinbruh6,
				this.a,
				Arrays.deepHashCode(this.b),
				Arrays.deepHashCode(this.c),
				Arrays.deepHashCode(this.d),
				Arrays.deepHashCode(this.e),
				Arrays.deepHashCode(this.primArr)
		);
	}

	public static class Component0 {
		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Component0;
		}
	}

	public static class Component {
		@Serialize
		public int thinbruh;

		public Component(int thinbruh) {
			this.thinbruh = thinbruh;
		}


		@Override
		public boolean equals(Object o) {
			return this == o
					|| o instanceof Component component
					&& this.thinbruh == component.thinbruh;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.thinbruh);
		}
	}

	static TestClass create() {
		Component[][] thinbruh = new Component[5][];
		for (int i = 0; i < thinbruh.length; i++) {
			thinbruh[i] = new Component[]{new Component(23), new Component(32 * i)};
		}

		int[][] thinbruh2 = {
				{534, 2341, 516},
				{-534, 2341, 852},
				{534, -2341, 796258},
				{-534, 2341, 98786325},
				{534, -2000000, 954789654}
		};


		return new TestClass(420, thinbruh, thinbruh2, 69, Long.MAX_VALUE, new Component(17),
				new TestClass(425, thinbruh, new int[][]{thinbruh2[3]}, 69_420, 123456789101010L, new Component(62), new Component0()));
	}
}
