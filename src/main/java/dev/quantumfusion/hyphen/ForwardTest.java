package dev.quantumfusion.hyphen;

import java.util.List;

public class ForwardTest {
	Forward forward1;

	public static class Forward {
		Forward1 forward1;
		Forward1 forward12;
		Forward1 forward13;
		Forward1 forward14;
		Forward1 forward15;
		Forward1 forward16;
		Forward1 forward17;
		Forward1 forward18;
		Forward1 forward19;
	}

	public static class Forward1 {
		Forward2<List<Integer>> forward1;
		Forward2<List<Integer>> forward12;
		Forward2<List<Integer>> forward13;
		Forward2<List<Integer>> forward14;
		Forward2<List<Integer>> forward15;
		Forward2<List<Integer>> forward16;
		Forward2<List<Integer>> forward17;
		Forward2<List<Integer>> forward18;
		Forward2<List<Integer>> forward19;
		Forward2<List<Integer>> forward10;
		Forward2<List<Integer>> forward110;
	}

	public static class Forward2<A> extends Forward3<A> {
		Forward3<A> forward31;
		Forward3<A> forward32;
		Forward3<A> forward33;
		Forward3<A> forward34;
		Forward3<A> forward35;
		Forward3<A> forward36;
		Forward3<A> forward37;
		Forward3<A> forward38;
		Forward3<A> forward39;
		Forward3<A> forward30;
		Forward3<A> forward311;
		Forward3<A> forward321;
		Forward3<A> forward331;
		Forward3<A> forward341;
		Forward3<A> forward351;
		Forward3<A> forward361;
		Forward3<A> forward371;
		Forward3<A> forward381;
		Forward3<A> forward391;
		Forward3<A> forward301;
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
