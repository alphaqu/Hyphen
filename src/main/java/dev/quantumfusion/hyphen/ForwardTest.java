package dev.quantumfusion.hyphen;

import java.util.List;

public class ForwardTest {
	Forward forward1;

	public static class Forward {
		Forward1 forward1;
	}

	public static class Forward1 {
		Forward2<List<Integer>> forward1;
	}

	public static class Forward2<A> extends Forward3<A> {
		Forward3<A> forward3;
	}

	public static class Forward3<A> {
		A thing;
		List<Integer>[] dfsad;
		Forward4 thingie;
	}

	public static class Forward4 {
		int thing;

	}
}
