package dev.notalpha.hyphen.test.poly.general;

import dev.notalpha.hyphen.scan.annotations.DataSubclasses;
import dev.notalpha.hyphen.test.poly.classes.c.C1;
import dev.notalpha.hyphen.test.poly.classes.c.C3Ignore;
import dev.notalpha.hyphen.util.TestSupplierUtil;
import dev.notalpha.hyphen.util.TestThis;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@TestThis
public class MissingTypeIgnore {
    @DataSubclasses({C1.class, C3Ignore.class})
    public C1<Integer> integer;


    public MissingTypeIgnore(C1<Integer> integer) {
        this.integer = integer;
    }

    public static Supplier<Stream<? extends MissingTypeIgnore>> generateMissingTypeIgnore() {
        return TestSupplierUtil.cross(TestSupplierUtil.subClasses(C1.generateC1(TestSupplierUtil.INTEGERS), C3Ignore.generateC3Ignore(TestSupplierUtil.INTEGERS)), MissingTypeIgnore::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        MissingTypeIgnore that = (MissingTypeIgnore) o;
        return Objects.equals(this.integer, that.integer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.integer);
    }

    @Override
    public String toString() {
        return "MissingTypeIgnore{" +
                "integer=" + this.integer +
                '}';
    }
}
