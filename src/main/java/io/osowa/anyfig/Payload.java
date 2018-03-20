package io.osowa.anyfig;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

public class Payload {

    public final Optional<Object> object;
    public final Configurable annotation;
    public final Field field;

    public Payload(Optional<Object> object, Configurable annotation, Field field) {
        if (object.isPresent() == Modifier.isStatic(field.getModifiers())) {
            if (object.isPresent()) {
                throw new IllegalArgumentException("Object supplied for static field");
            } else {
                throw new IllegalArgumentException("Object missing for instance field");
            }
        }
        this.object = object;
        this.annotation = annotation;
        this.field = field;
    }

}
