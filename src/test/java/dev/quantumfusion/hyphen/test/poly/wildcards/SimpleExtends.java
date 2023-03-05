package dev.quantumfusion.hyphen.test.poly.wildcards;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.test.poly.classes.c.C1;
import dev.quantumfusion.hyphen.test.poly.classes.c.C2;
import dev.quantumfusion.hyphen.thr.UnknownTypeException;
import dev.quantumfusion.hyphen.util.TestThis;
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
