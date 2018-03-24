package io.osowa.anyfig;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PropertyMechanism extends KeyValueMechanism {

    private static final SimpleMap STANDARD_GET_PROPERTY = key -> Optional.ofNullable(System.getProperty(key));

    private static SimpleMap getProperty = STANDARD_GET_PROPERTY;

    @Override protected SimpleMap makeMap(String[] ignored) {
        return getProperty;
    }

    @Override
    protected List<String> makeCandidates(Field field, Configurable annotation) {
        List<String> candidates = new ArrayList<>(2);
        String property = annotation.property();
        if (property.isEmpty()) {
            candidates.add(field.getName()); // someField
            candidates.add(Utils.encodeField(field)); // some.package.SomeClass.someField
        } else {
            candidates.add(property);
        }
        return candidates;
    }

    // intended for test code; normal clients do not need this method
    public static void withProperties(Utils.FallibleRunnable action, String... strings) throws Exception {
        withTemporaryMap(
            action,
            gp -> getProperty = gp,
            () -> STANDARD_GET_PROPERTY,
            strings);
    }

}
