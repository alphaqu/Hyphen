package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.FailTest;
import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C1;
import dev.notalpha.hyphen.test.poly.classes.c.C3;
import dev.notalpha.hyphen.util.TestThis;

@FailTest(/*UnknownTypeException.class*/)
@TestThis
public class MissingTypeFail {
    @DataSubclasses({C1.class, C3.class})
    public C1<Integer> integer;


    public MissingTypeFail(C1<Integer> integer) {
        this.integer = integer;
    }
}
