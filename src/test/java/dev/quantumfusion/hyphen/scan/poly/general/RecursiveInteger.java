package dev.quantumfusion.hyphen.scan.poly.general;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.scan.annotations.DataSubclasses;
import dev.quantumfusion.hyphen.scan.poly.classes.c.C1;
import dev.quantumfusion.hyphen.scan.poly.classes.c.RecursiveC;
import dev.quantumfusion.hyphen.util.TestSupplierUtil;
import dev.quantumfusion.hyphen.util.TestThis;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.INTEGERS;
import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

@TestThis
@FailTest // FIXME infinite toString()
public class RecursiveInteger {
    //@DataSubclasses({C1.class, RecursiveC.class})
    public C1<Integer> data;

    public RecursiveInteger(C1<Integer> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        RecursiveInteger recursive = (RecursiveInteger) o;

        return this.data.equals(recursive.data);
    }

    @Override
    public int hashCode() {
        return this.data.hashCode();
    }

  //  public static Supplier<? extends Stream<? extends RecursiveInteger>> generateRecursiveInteger(
  //          int depth
  //  ) {
  //      return cross(TestSupplierUtil.<C1<Integer>>subClasses(
  //                      C1.generateC1(INTEGERS),
  //                      RecursiveC.generateRecursiveC(INTEGERS, depth - 1))
  //              , RecursiveInteger::new);
  //  }

    @Override
    public String toString() {
        return "Recursive{" +
                "data=" +
                '}';
    }
}
