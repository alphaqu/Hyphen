package dev.notalpha.hyphen.test.poly.wildcards;

import dev.notalpha.hyphen.FailTest;
import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C1;
import dev.notalpha.hyphen.test.poly.classes.c.C2;
import dev.notalpha.hyphen.thr.HyphenException;
import dev.notalpha.hyphen.util.TestThis;

@TestThis
@FailTest(HyphenException.class)
public class SimpleSuper {
    @DataSubclasses({C1.class, C2.class})
    public C1<? super Number> data;

    public SimpleSuper(C1<? super Number> data) {
        this.data = data;
    }
}
