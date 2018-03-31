package io.osowa.anyfig;

import io.osowa.anyfig.mechanisms.Mechanism;
import io.osowa.anyfig.mechanisms.Mechanisms;
import io.osowa.anyfig.mechanisms.RootMechanism;
import io.osowa.anyfig.utils.Pair;
import io.osowa.anyfig.utils.Possible;

import java.lang.reflect.Field;

public class Retriever {

    private final Mechanism rootMechanism = new RootMechanism();

    public Possible<Pair<Object,Mechanisms>> retrieve(Field field, Configurable annotation, String[] args) throws Exception {
        return rootMechanism.apply(field, annotation, args);
    }

}

