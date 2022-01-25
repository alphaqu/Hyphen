package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C3Def;
import dev.quantumfusion.hyphen.util.TestThis;

@TestThis
public class UnknownInbetweenType {
    @DataSubclasses({C1.class, C3Def.class})
    public C1<Integer> integer;


    public UnknownInbetweenType(C1<Integer> integer) {
        this.integer = integer;
    }
}
