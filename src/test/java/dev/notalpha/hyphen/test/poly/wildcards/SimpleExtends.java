package dev.notalpha.hyphen.test.poly.wildcards;

import dev.notalpha.hyphen.FailTest;
import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C1;
import dev.notalpha.hyphen.test.poly.classes.c.C2;
import dev.notalpha.hyphen.util.TestThis;
import org.opentest4j.TestSkippedException;

@TestThis
// TODO: implement wildcards
@FailTest(TestSkippedException.class)
public class SimpleExtends {
    @DataSubclasses({C1.class, C2.class})
    public C1<? extends @DataSubclasses({Integer.class, Float.class}) Number> data;

    public SimpleExtends(C1<? extends Number> data) {
        this.data = data;
    }
}
