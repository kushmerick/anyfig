package io.osowa.anyfig;


import java.lang.reflect.Field;
import java.util.List;

public abstract class SequentialMechanism implements Mechanism {

    abstract List<Mechanism> getMechanisms();

    @Override public Possible<Object> apply(Field field, Configurable annotation, String[] args) {
        return
            getMechanisms().stream()
            .map(mechanism -> mechanism.apply(field, annotation, args))
            .filter(value -> value.present())
            .findFirst()
            .orElse(Possible.absent());
    }

}
