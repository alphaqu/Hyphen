package dev.quantumfusion.hyphen.test.simple;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.util.TestThis;

@TestThis
@FailTest(/*AccessException.class*/)
public class FieldIsNotPublic {
    Object thing;

    public FieldIsNotPublic(Object thing) {
        this.thing = thing;
    }

}
