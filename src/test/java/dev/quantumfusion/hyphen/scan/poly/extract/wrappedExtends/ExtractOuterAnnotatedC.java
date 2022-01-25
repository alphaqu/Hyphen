package dev.quantumfusion.hyphen.scan.poly.extract.wrappedExtends;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C2;
import dev.quantumfusion.hyphen.scan.poly.classes.c.CoWrappedC1Extends;
import dev.quantumfusion.hyphen.thr.UnknownTypeException;
import dev.quantumfusion.hyphen.util.TestThis;

@TestThis
// TODO: This is currently not supported in the new scan system
@FailTest(UnknownTypeException.class)
public class ExtractOuterAnnotatedC {
    @DataSubclasses({C1.class, CoWrappedC1Extends.class})
    public C1<@DataSubclasses({C1.class, C2.class}) C1<Integer>> data;

    public ExtractOuterAnnotatedC(C1<C1<Integer>> data) {
        this.data = data;
    }
}
