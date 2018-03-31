package io.osowa.anyfig.mechanisms;

import io.osowa.anyfig.Configurable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArgsMechanism extends KeyValueMechanism {

    private static final String PREFIX = "--";
    private static final String SEPARATOR = "=";

    @Override
    public Mechanisms getMechanism() {
        return Mechanisms.ARGUMENT;
    }

    @Override
    protected SimpleMap makeMap(String[] args) {
        return key -> {
            Optional<String> keyval = Stream.of(args).filter(arg -> arg.startsWith(key)).findAny();
            if (keyval.isPresent()) {
                return Optional.of(keyval.get().substring(key.length()));
            } else {
                return Optional.empty();
            }
        };
    }

    @Override
    protected List<String> makeCandidates(Field field, Configurable annotation) {
        List<String> candidates = new ArrayList<>(2);
        String argument = annotation.argument();
        if (argument.isEmpty()) {
            candidates.add(field.getName()); // someField
            candidates.add(field.getDeclaringClass().getName() + '.' + field.getName());
        } else {
            candidates.add(argument);
        }
        return candidates.stream().map(candidate -> PREFIX + candidate + SEPARATOR).collect(Collectors.toList());
    }

}
