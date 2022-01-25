package dev.quantumfusion.hyphen.scan.simple;

import dev.quantumfusion.hyphen.FailTest;
import dev.quantumfusion.hyphen.util.TestThis;

@TestThis
@FailTest(/*AccessException.class*/)
public class PathTest {
    public Thing1 superGaming;

    public PathTest(Thing1 superGaming) {
        this.superGaming = superGaming;
    }

    public static class Thing1 {
        public Thing2 rice;

        public Thing1(Thing2 rice) {
            this.rice = rice;
        }


        public static class Thing2 {
            public Thing3 model;

            public Thing2(Thing3 model) {
                this.model = model;
            }


            public static class Thing3 {
                public Thing4 block;

                public Thing3(Thing4 block) {
                    this.block = block;
                }


                public static class Thing4 {
                    public Thing2 thing1;
                }
            }
        }
    }
}
