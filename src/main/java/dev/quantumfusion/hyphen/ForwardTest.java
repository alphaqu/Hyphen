package dev.quantumfusion.hyphen;

import dev.quantumfusion.hyphen.annotations.Ser;

import java.util.List;

public class ForwardTest {
	Forward forward1;

	public static class Forward {
		@Ser
		Forward1 forward1;
		@Ser
		int[] is;
		@Ser
		Number[] ns;
		Integer i;
	}

	@Ser
	public static class Forward1 {
		@Ser
		Forward2<List<Integer>> forward1;
	}

	public static class Forward2<A> extends Forward3<A> {
		Forward3<A> forward31;
	}

	// @Ser
	public static class Forward3<A> {
		Forward3<Forward3<A>> f;
		A thing;
		List<Integer>[] dfsad;
		Forward4 thingie;
	}

	public static class Forward4 {
		int thing;

	}
}
