package dev.quantumfusion.hyphen.scan.poly.wildcards;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C2;
import dev.quantumfusion.hyphen.thr.UnknownTypeException;
import dev.quantumfusion.hyphen.util.TestThis;

@TestThis
// TODO: implement wildcards
@FailTest(UnknownTypeException.class)
public class SimpleExtends {
    @DataSubclasses({C1.class, C2.class})
    public C1<? extends @DataSubclasses({Integer.class, Float.class}) Number> data;

    public SimpleExtends(C1<? extends Number> data) {
        this.data = data;
    }
}
