package io.osowa.anyfig.mechanisms;


import io.osowa.anyfig.Configurable;
import io.osowa.anyfig.utils.Pair;
import io.osowa.anyfig.utils.Possible;

import java.lang.reflect.Field;
import java.util.List;

public abstract class SequentialMechanism implements Mechanism {

    abstract List<Mechanism> getMechanisms();

    @Override public Possible<Pair<Object,Mechanisms>> apply(Field field, Configurable annotation, String[] args) {
        return
            getMechanisms().stream()
            .map(mechanism -> mechanism.apply(field, annotation, args))
            .filter(value -> value.present())
            .findFirst()
            .orElse(Possible.absent());
    }

}
