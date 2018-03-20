package io.osowa.anyfig;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;

// like Optional, but permit null

public class Possible<T> {

    private final static Possible<?> ABSENT = new Possible<>();

    public static Possible absent() {
        return ABSENT;
    }

    public static <T> Possible of(T t) {
        return new Possible(t);
    }

    private final boolean present;
    private final T thing;

    private Possible(T t) {
        this(true, t);
    }

    private Possible() {
        this(false, null);
    }

    private Possible(boolean present, T thing) {
        this.present = present;
        this.thing = thing;
    }

    public boolean present() {
        return present;
    }

    public T get() {
        if (present) {
            return thing;
        } else {
            throw new NoSuchElementException("No value present");
        }
    }

    public T orElse(T thing) {
        return present ? this.thing : thing;
    }

}
