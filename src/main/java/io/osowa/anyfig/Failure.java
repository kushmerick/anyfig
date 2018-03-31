package io.osowa.anyfig;

import io.osowa.anyfig.mechanisms.Mechanisms;
import io.osowa.anyfig.utils.Possible;

import java.lang.reflect.Field;
import java.util.Optional;

public class Failure extends Payload {

    public Possible<Object> oldVal;
    public Possible<Object> newVal;
    public Exception exception;

    public Failure(Optional<Object> object, Configurable annotation, Field field, Mechanisms mechanism, Possible<Object> oldVal, Possible<Object> newVal, Exception exception) {
        super(object, annotation, field, mechanism);
        this.oldVal = oldVal;
        this.newVal = newVal;
        this.exception = exception;
    }

}
