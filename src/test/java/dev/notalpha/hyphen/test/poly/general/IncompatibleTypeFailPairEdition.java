package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.FailTest;
import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.pair.Pair;
import dev.notalpha.hyphen.test.poly.classes.pair.SelfPair;
import dev.notalpha.hyphen.util.TestThis;

@FailTest(/*IncompatibleTypeException.class*/)
@TestThis
public class IncompatibleTypeFailPairEdition {
    @DataSubclasses({Pair.class, SelfPair.class})
    public Pair<Float, Integer> data;


    public IncompatibleTypeFailPairEdition(Pair<Float, Integer> data) {
        this.data = data;
    }
}
