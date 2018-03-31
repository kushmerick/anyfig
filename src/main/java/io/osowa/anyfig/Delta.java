package io.osowa.anyfig;

import io.osowa.anyfig.mechanisms.Mechanisms;

import java.lang.reflect.Field;
import java.util.Optional;

public class Delta extends Payload {

    public final Object oldVal;
    public final Object newVal;

    public Delta(Optional<Object> object, Configurable annotation, Field field, Mechanisms mechanism, Object oldVal, Object newVal) {
        super(object, annotation, field, mechanism);
        this.oldVal = oldVal;
        this.newVal = newVal;
    }

}
