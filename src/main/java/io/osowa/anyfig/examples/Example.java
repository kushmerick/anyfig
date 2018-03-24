package io.osowa.anyfig.examples;

import io.osowa.anyfig.Anyfig;
import io.osowa.anyfig.Delta;

import java.util.concurrent.CountDownLatch;

// This is the example from /README.md.  To explore Anyfig, run it like:

// ANYFIG_REMOTE_API_TOKEN=s3kret MIN_SPEED=10 java -DmaxVehicles=20 -jar anyfig-1.0-SNAPSHOT-jar-with-dependencies.jar --mode=RIGHT

public class Example {

    static class Settings {
        static int maxVehicles;
        static double minSpeed;
        enum Mode { LEFT, RIGHT };
        private static final Mode DEFAULT_MODE = Mode.LEFT;
        static Mode mode;
    }

    static void callback(Delta delta) {
        System.out.println("Configuring `" + delta.field + "` to `" + delta.newVal + '`');
    }

    public static void main(String[] args) throws Exception {
        Anyfig anyfig = new Anyfig();
        anyfig.configure(Example::callback, args, Settings.class);
        anyfig.enableRemoteAPI();
        (new CountDownLatch(1)).await();
    }

}
