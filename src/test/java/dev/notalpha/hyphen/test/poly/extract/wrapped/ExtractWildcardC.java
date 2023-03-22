package dev.notalpha.hyphen.test.poly.extract.wrapped;

import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C1;
import dev.notalpha.hyphen.test.poly.classes.c.CoWrappedC1;
import dev.notalpha.hyphen.util.TestThis;

@TestThis
public class ExtractWildcardC {
    @DataSubclasses({C1.class, CoWrappedC1.class})
    public C1<? extends C1<Integer>> data;

    public ExtractWildcardC(C1<? extends C1<Integer>> data) {
        this.data = data;
    }
}
