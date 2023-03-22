package dev.notalpha.hyphen.test.poly.extract.wrappedExtends;

import dev.notalpha.hyphen.FailTest;
import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C1;
import dev.notalpha.hyphen.test.poly.classes.c.C2;
import dev.notalpha.hyphen.test.poly.classes.c.CoWrappedC1Extends;
import dev.notalpha.hyphen.thr.UnknownTypeException;
import dev.notalpha.hyphen.util.TestThis;

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
