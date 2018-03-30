package io.osowa.anyfig;

import com.google.common.base.CaseFormat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ConstMechanism extends SequentialMechanism {

    private final Mechanism defaultConstMechanism = (field, annotation, args) -> {
        if (!annotation.constant().isEmpty()) {
            return Possible.absent();
        }
        String constant = field.getName(); // someField
        constant = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, constant); // SOME_FIELD
        final String DEFAULT_CONST_PREFIX = "DEFAULT_";
        constant = DEFAULT_CONST_PREFIX + constant; // DEFAULT_SOME_FIELD
        try {
            return tryConstant(constant, field.getDeclaringClass().getName());
        } catch (Exception ignored) {
            // we guessed for the name of the constant, but couldn't find it; no big deal, just proceed to the other mechanisms
            // TODO: Interesting... Our tests have zero coverage here??!!
            return Possible.absent();
        }
    };

    private final Mechanism customConstMechanism = (field, annotation, args) -> {
        String constant = annotation.constant();
        if (constant.isEmpty()) {
            return Possible.absent();
        }
        String className = field.getDeclaringClass().getName(); // either SOME_CONST or some.pkg.Class.SOME_CONST
        if (constant.contains(".")) {
            Pair<String,String> decoded = Utils.decodeFieldKey(constant);
            className = decoded.left; // some.pkg.Class
            constant = decoded.right; // SOME_CONST
        }
        Possible<Pair<Object,Mechanisms>> pair;
        try {
             pair = tryConstant(constant, className);
        } catch (Exception exception) {
            // TODO: It seems weird to have both this throw and the one a few lines down?!!?
            throw new ConfigurationException("Failure while getting constant `" + annotation.constant() + '`', exception);
        }
        if (pair.present()) {
            return pair;
        } else {
            // TODO: It seems weird to have both this throw and the one a few lines up!??!
            throw new ConfigurationException("Unable to find constant `" + annotation.constant() + '`');
        }
    };

    private Possible<Pair<Object,Mechanisms>> tryConstant(String constant, String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        // yes, it would be simpler to use `clazz.getDeclaredField(constant)` but that
        // doesn't work for private fields (?!!?), so we search for it instead:
        for (Field field : clazz.getDeclaredFields()) {
            // note that we intentionally don't actually verify that the field is `final`
            if (field.getName().equals(constant) && Utils.isStatic(field)) {
                return Possible.of(Pair.of(Utils.getField(field), Mechanisms.CONSTANT));
            }
        }
        return Possible.absent();
    }

    private final List<Mechanism> mechanisms = Arrays.asList(
        defaultConstMechanism,
        customConstMechanism
    );

    @Override
    List<Mechanism> getMechanisms() {
        return mechanisms;
    }

}


