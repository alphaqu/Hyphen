package dev.notalpha.hyphen.test.poly.general;


import dev.notalpha.hyphen.FailTest;
import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C1;
import dev.notalpha.hyphen.test.poly.classes.c.C2;
import dev.notalpha.hyphen.util.TestThis;
import dev.notalpha.hyphen.util.ValueGenerator;

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
