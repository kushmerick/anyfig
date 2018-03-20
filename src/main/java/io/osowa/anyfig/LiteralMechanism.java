package io.osowa.anyfig;

import java.lang.reflect.Field;

public class LiteralMechanism implements Mechanism {

    @Override
    public Possible<Object> apply(Field field, Configurable annotation, String[] args) {
        if (annotation.literal()) {
            String value = annotation.value();
            if (value.isEmpty()) {
                return Possible.of(annotation.NULL() ? null : "");
            } else {
                return Possible.of(value);
            }
        } else {
            return Possible.absent();
        }
    }

}
