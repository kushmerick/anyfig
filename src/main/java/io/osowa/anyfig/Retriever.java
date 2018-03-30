package io.osowa.anyfig;

import java.lang.reflect.Field;

public class Retriever {

    private final Mechanism rootMechanism = new RootMechanism();

    public Possible<Pair<Object,Mechanisms>> retrieve(Field field, Configurable annotation, String[] args) throws Exception {
        return rootMechanism.apply(field, annotation, args);
    }

}

