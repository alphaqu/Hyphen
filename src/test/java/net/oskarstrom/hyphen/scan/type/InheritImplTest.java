package net.oskarstrom.hyphen.scan.type;

import net.oskarstrom.hyphen.Description;
import net.oskarstrom.hyphen.annotation.Serialize;

import java.util.ArrayList;

@Description("Tests if it can find an inherited implementation")
public class InheritImplTest {
	@Serialize
	public ArrayList<Integer> integers;

	public InheritImplTest(ArrayList<Integer> integers) {
		this.integers = integers;
	}

}
