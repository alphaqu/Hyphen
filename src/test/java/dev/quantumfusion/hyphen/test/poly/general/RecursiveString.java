package dev.quantumfusion.hyphen.test.poly.general;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.test.poly.classes.c.C1;
import dev.quantumfusion.hyphen.util.TestThis;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

@TestThis
@FailTest // FIXME infinite toString()
public class RecursiveString {
    //@DataSubclasses({C1.class, RecursiveC.class})
    public C1<String> data;

    public RecursiveString(C1<String> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        RecursiveString recursive = (RecursiveString) o;

        return this.data.equals(recursive.data);
    }

    @Override
    public int hashCode() {
        return this.data.hashCode();
    }

   //public static Supplier<? extends Stream<? extends RecursiveString>> generateRecursiveString(
   //        int depth
   //) {
   //    return cross(TestSupplierUtil.<C1<String>>subClasses(
   //                    C1.generateC1(STRINGS),
   //                    RecursiveC.generateRecursiveC(STRINGS, depth - 1))
   //            , RecursiveString::new);
   //}

    @Override
    public String toString() {
        return "Recursive{" +
                "data=" +
                '}';
    }
}
