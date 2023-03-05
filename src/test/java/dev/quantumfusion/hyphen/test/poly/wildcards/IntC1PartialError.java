package dev.quantumfusion.hyphen.test.poly.wildcards;

import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.test.poly.classes.c.C1;
import dev.quantumfusion.hyphen.test.poly.classes.c.IntC1;
import dev.quantumfusion.hyphen.util.TestThis;

@TestThis
public class IntC1PartialError {
    @DataSubclasses({C1.class, IntC1.class})
    public C1<@DataSubclasses({Integer.class, Float.class}) ? extends Number> data;

    public IntC1PartialError(C1<? extends Number> data) {
        this.data = data;
    }
}
