package dev.notalpha.hyphen.test.poly.extract.wrapped;

import dev.notalpha.hyphen.FailTest;
import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C1;
import dev.notalpha.hyphen.test.poly.classes.c.C2;
import dev.notalpha.hyphen.test.poly.classes.c.CoWrappedC1;
import dev.notalpha.hyphen.thr.UnknownTypeException;
import dev.notalpha.hyphen.util.TestThis;

@TestThis
// TODO: This is currently not supported in the new scan system
@FailTest(UnknownTypeException.class)
public class ExtractExtendsC {
    @DataSubclasses({C1.class, CoWrappedC1.class})
    public C1<C2<Integer>> data;

    public ExtractExtendsC(C1<C2<Integer>> data) {
        this.data = data;
    }
}
