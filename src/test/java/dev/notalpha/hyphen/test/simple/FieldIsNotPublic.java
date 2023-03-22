package dev.notalpha.hyphen.test.simple;

import dev.notalpha.hyphen.FailTest;
import dev.notalpha.hyphen.util.TestThis;

@TestThis
@FailTest(/*AccessException.class*/)
public class FieldIsNotPublic {
    Object thing;

    public FieldIsNotPublic(Object thing) {
        this.thing = thing;
    }

}
