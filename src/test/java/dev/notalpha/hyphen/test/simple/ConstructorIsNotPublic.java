package dev.notalpha.hyphen.test.simple;

import dev.notalpha.hyphen.FailTest;
import dev.notalpha.hyphen.util.TestThis;

@FailTest(/*AccessException.class*/)
@TestThis
public class ConstructorIsNotPublic {
    public int prim;

    ConstructorIsNotPublic(int prim) {
        this.prim = prim;
    }
}
