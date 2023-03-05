package dev.quantumfusion.hyphen.test.poly.extract.wrappedSuper;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.test.poly.classes.c.C1;
import dev.quantumfusion.hyphen.test.poly.classes.c.CoWrappedC1Super;
import dev.quantumfusion.hyphen.thr.UnknownTypeException;
import dev.quantumfusion.hyphen.util.TestThis;

@TestThis
// TODO: This is currently not supported in the new scan system
@FailTest(UnknownTypeException.class)
public class ExtractWildcardC {
    @DataSubclasses({C1.class, CoWrappedC1Super.class})
    public C1<? extends C1<Integer>> data;

    public ExtractWildcardC(C1<? extends C1<Integer>> data) {
        this.data = data;
    }
}
