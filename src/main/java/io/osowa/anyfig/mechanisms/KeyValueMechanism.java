package io.osowa.anyfig.mechanisms;

import io.osowa.anyfig.Configurable;
import io.osowa.anyfig.utils.Pair;
import io.osowa.anyfig.utils.Possible;
import io.osowa.anyfig.utils.Utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class KeyValueMechanism implements Mechanism {

    protected interface SimpleMap {
        Optional<String> get(String key);
    }

    abstract protected SimpleMap makeMap(String[] args);

    abstract protected List<String> makeCandidates(Field field, Configurable annotation);

    abstract Mechanisms getMechanism();

    @Override
    public Possible<Pair<Object,Mechanisms>> apply(Field field, Configurable annotation, String[] args) {
        SimpleMap map = makeMap(args);
        for (String key: makeCandidates(field, annotation)) {
            Optional<String> value = map.get(key);
            if (value.isPresent()) {
                return Possible.of(Pair.of(value.get(), getMechanism()));
            }
        }
        return Possible.absent();
    }

    // for testing
    protected static void withTemporaryMap(
            Utils.FallibleRunnable action,
            Consumer<SimpleMap> mapSetter,
            Supplier<SimpleMap> standardMapGetter,
            String [] pairs)
            throws Exception
    {
        Map<String,String> map = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put(pairs[i], pairs[i+1]);
        }
        mapSetter.accept(key -> Optional.ofNullable(map.get(key)));
        try {
            action.run();
        } finally {
            mapSetter.accept(standardMapGetter.get());
        }
    }

}
