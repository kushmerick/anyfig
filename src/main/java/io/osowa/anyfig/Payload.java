package io.osowa.anyfig;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.function.Supplier;

public class Payload {

    public static Supplier<Long> clock;

    public static void resetClock() {
        clock = () -> System.currentTimeMillis();
    }

    static {
        resetClock();
    }

    public long timestamp = clock.get();
    public final Optional<Object> object;
    public final Configurable annotation;
    public final Field field;
    public final Mechanisms mechanism;

    public Payload(Optional<Object> object, Configurable annotation, Field field, Mechanisms mechanism) {
        if (object.isPresent() == Utils.isStatic(field)) {
            if (object.isPresent()) {
                throw new IllegalArgumentException("Object supplied for static field");
            } else {
                throw new IllegalArgumentException("Object missing for instance field");
            }
        }
        this.object = object;
        this.annotation = annotation;
        this.field = field;
        this.mechanism = mechanism;
    }

}
