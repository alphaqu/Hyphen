package dev.quantumfusion.hyphen.test.poly.wildcards;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.test.poly.classes.c.C1;
import dev.quantumfusion.hyphen.test.poly.classes.c.C2;
import dev.quantumfusion.hyphen.thr.HyphenException;
import dev.quantumfusion.hyphen.thr.UnknownTypeException;
import dev.quantumfusion.hyphen.util.TestThis;

@TestThis
@FailTest(HyphenException.class)
public class SimpleSuper {
    @DataSubclasses({C1.class, C2.class})
    public C1<? super Number> data;

    public SimpleSuper(C1<? super Number> data) {
        this.data = data;
    }
}
