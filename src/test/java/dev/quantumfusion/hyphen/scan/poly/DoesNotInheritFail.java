package dev.quantumfusion.hyphen.scan.poly;


<<<<<<< HEAD:src/test/java/dev/quantumfusion/hyphen/scan/poly/DoesNotInheritFail.java
import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;
import dev.quantumfusion.hyphen.thr.IllegalInheritanceException;
=======
import net.oskarstrom.hyphen.FailTest;
import net.oskarstrom.hyphen.annotation.SerSubclasses;
import net.oskarstrom.hyphen.annotation.Serialize;
import net.oskarstrom.hyphen.scan.poly.classes.C1;
import net.oskarstrom.hyphen.scan.poly.classes.C2;
import net.oskarstrom.hyphen.thr.IllegalInheritanceException;
>>>>>>> origin/union-types:src/test/java/net/oskarstrom/hyphen/scan/poly/DoesNotInheritFail.java

import java.util.List;

@FailTest(IllegalInheritanceException.class)
public class DoesNotInheritFail {
	@Serialize
	@SerSubclasses({C1.class, C2.class})
	public List<Integer> test;

	public DoesNotInheritFail(List<Integer> test) {
		this.test = test;
	}
}
