package dev.quantumfusion.hyphen.scan.poly.extract.wrapped;

import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C2;
import dev.quantumfusion.hyphen.scan.poly.classes.c.CoWrappedC1;
import dev.quantumfusion.hyphen.util.TestThis;

@TestThis
public class ExtractBothAnnotatedC {
    @DataSubclasses({C1.class, CoWrappedC1.class})
    public C1<@DataSubclasses({C1.class, C2.class}) C1<@DataSubclasses({Float.class, Integer.class}) Number>> data;

    public ExtractBothAnnotatedC(C1<C1<Number>> data) {
        this.data = data;
    }
}
