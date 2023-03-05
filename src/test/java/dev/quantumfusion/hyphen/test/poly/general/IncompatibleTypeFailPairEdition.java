package dev.quantumfusion.hyphen.test.poly.general;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.test.poly.classes.pair.Pair;
import dev.quantumfusion.hyphen.test.poly.classes.pair.SelfPair;
import dev.quantumfusion.hyphen.util.TestThis;

@FailTest(/*IncompatibleTypeException.class*/)
@TestThis
public class IncompatibleTypeFailPairEdition {
    @DataSubclasses({Pair.class, SelfPair.class})
    public Pair<Float, Integer> data;


    public IncompatibleTypeFailPairEdition(Pair<Float, Integer> data) {
        this.data = data;
    }
}
