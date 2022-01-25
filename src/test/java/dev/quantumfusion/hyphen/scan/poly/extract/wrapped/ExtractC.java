package dev.quantumfusion.hyphen.scan.poly.extract.wrapped;

import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.c.CoWrappedC1;
import dev.quantumfusion.hyphen.util.TestThis;

@TestThis
public class ExtractC {
    @DataSubclasses({C1.class, CoWrappedC1.class})
    public C1<C1<Integer>> data;

    public ExtractC(C1<C1<Integer>> data) {
        this.data = data;
    }
}
