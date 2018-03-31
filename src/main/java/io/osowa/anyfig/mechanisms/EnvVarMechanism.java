package io.osowa.anyfig.mechanisms;

import com.google.common.base.CaseFormat;

import io.osowa.anyfig.Configurable;
import io.osowa.anyfig.utils.Utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EnvVarMechanism extends KeyValueMechanism {

    private static final SimpleMap STANDARD_GET_ENV_VAR = key -> Optional.ofNullable(System.getenv(key));

    private static SimpleMap getEnvVar = STANDARD_GET_ENV_VAR;

    @Override
    public Mechanisms getMechanism() {
        return Mechanisms.ENVVAR;
    }

    @Override
    protected SimpleMap makeMap(String[] ignored) {
        return getEnvVar;
    }

    @Override
    protected List<String> makeCandidates(Field field, Configurable annotation) {
        String envVar = annotation.envvar();
        if (envVar.isEmpty()) {
            envVar = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, field.getName()); // someField -> SOME_FIELD
        }
        return Arrays.asList(envVar);
    }

    // intended for test code; normal clients do not need this method
    public static void withProperties(Utils.FallibleRunnable action, String... strings) throws Exception {
        withTemporaryMap(
            action,
            gev -> getEnvVar = gev,
            () -> STANDARD_GET_ENV_VAR,
            strings);
    }

}

