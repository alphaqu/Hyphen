package dev.quantumfusion.hyphen.test.poly.general;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.test.poly.classes.c.C1;
import dev.quantumfusion.hyphen.test.poly.classes.c.IntC1;
import dev.quantumfusion.hyphen.util.TestThis;


@FailTest(/*IncompatibleTypeException.class*/)
@TestThis
public class IncompatibleTypeFail {
    @DataSubclasses({C1.class, IntC1.class})
    public C1<Float> floatC1;


    public IncompatibleTypeFail(C1<Float> floatC1) {
        this.floatC1 = floatC1;
    }
}
