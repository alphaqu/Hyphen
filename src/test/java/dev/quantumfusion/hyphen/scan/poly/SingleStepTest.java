package dev.quantumfusion.hyphen.scan.poly;

<<<<<<< HEAD:src/test/java/dev/quantumfusion/hyphen/scan/poly/SingleStepTest.java
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
=======
import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.C1;
import net.oskarstrom.hyphen.scan.poly.classes.C2;
>>>>>>> origin/union-types:src/test/java/net/oskarstrom/hyphen/scan/poly/SingleStepTest.java

public class SingleStepTest {
	@Serialize
	@SerSubclasses({C1.class, C2.class})
	public C1<Integer> integer;


	public SingleStepTest(C1<Integer> integer) {
		this.integer = integer;
	}
}
