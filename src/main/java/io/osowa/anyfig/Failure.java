package io.osowa.anyfig;

import java.lang.reflect.Field;
import java.util.Optional;

public class Failure extends Payload {

    public Possible<Object> oldVal;
    public Possible<Object> newVal;
    public Exception exception;

    public Failure(Optional<Object> object, Configurable annotation, Field field, Possible<Object> oldVal, Possible<Object> newVal, Exception exception) {
        super(object, annotation, field);
        this.oldVal = oldVal;
        this.newVal = newVal;
        this.exception = exception;
    }

}
