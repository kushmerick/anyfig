package io.osowa.anyfig.mechanisms;

import io.osowa.anyfig.Configurable;
import io.osowa.anyfig.utils.Pair;
import io.osowa.anyfig.utils.Possible;

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
