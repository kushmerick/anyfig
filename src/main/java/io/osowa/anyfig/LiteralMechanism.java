package io.osowa.anyfig;

import java.lang.reflect.Field;

public class LiteralMechanism implements Mechanism {

    @Override
    public Possible<Pair<Object,Mechanisms>> apply(Field field, Configurable annotation, String[] args) {
        if (annotation.literal()) {
            String value = annotation.value();
            if (value.isEmpty() && annotation.NULL()) {
                value = null;
            }
            return Possible.of(Pair.of(value, Mechanisms.LITERAL));
        } else {
            return Possible.absent();
        }
    }

}
