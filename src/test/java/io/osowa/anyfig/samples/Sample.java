package io.osowa.anyfig.samples;

import io.osowa.anyfig.Anyfig;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class Sample {

    public static final Logger logger = Logger.getLogger(Sample.class.getName());

    public static class Stuff {
        private static final int DEFAULT_FOO = 123;
        private static int foo;
    }

    public static void main(String[] args) throws Exception {
        Anyfig anyfig = new Anyfig();
        anyfig.enableRemoteAPI();
        Stuff stuff = new Stuff();
        anyfig.configure(logger, stuff);
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }

}
