package dev.notalpha.hyphen.test.simple;

import dev.notalpha.hyphen.FailTest;
import dev.notalpha.hyphen.util.TestThis;


@TestThis
@FailTest // FIXME
public class FieldIsNotPublicButDef {
    public int thing;

    private int ignore;


    public FieldIsNotPublicButDef(int thing) {
        this.thing = thing;
    }
}
