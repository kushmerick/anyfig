package io.osowa.anyfig.mechanisms;

import io.osowa.anyfig.Configurable;
import io.osowa.anyfig.utils.Pair;
import io.osowa.anyfig.utils.Possible;

import java.lang.reflect.Field;

// a Mechanism is a procedure for obtaining a field's value

public interface Mechanism {

    Possible<Pair<Object,Mechanisms>> apply(Field field, Configurable annotation, String[] args);

}
