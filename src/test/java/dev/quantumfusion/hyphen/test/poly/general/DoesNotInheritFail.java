package dev.quantumfusion.hyphen.test.poly.general;


import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.test.poly.classes.c.C1;
import dev.quantumfusion.hyphen.test.poly.classes.c.C2;
import dev.quantumfusion.hyphen.util.TestThis;
import dev.quantumfusion.hyphen.util.ValueGenerator;

import java.util.List;

@FailTest(/*IllegalInheritanceException.class*/)
@TestThis
public class DoesNotInheritFail {
    @DataSubclasses({C1.class, C2.class})
    public List<Integer> test;

    public DoesNotInheritFail(List<Integer> test) {
        this.test = test;
    }

    public static DoesNotInheritFail generate(ValueGenerator g) {
        return new DoesNotInheritFail(g.genList(g::genInt));
    }
}
