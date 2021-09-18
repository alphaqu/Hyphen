package net.oskarstrom.hyphen.scan.type;

import net.oskarstrom.hyphen.Description;
import net.oskarstrom.hyphen.annotation.Serialize;

import java.util.List;

@Description("Tests if it can find an implementation")
public class SimpleTypeTest {
	@Serialize
	public List<Integer> integers;

	public SimpleTypeTest(List<Integer> integers) {
		this.integers = integers;
	}
}
