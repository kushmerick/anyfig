package io.osowa.anyfig.mechanisms;

import java.util.Arrays;
import java.util.List;

public class RootMechanism extends SequentialMechanism {

    private final List<Mechanism> policies = Arrays.asList(
        new EnvVarMechanism(),
        new PropertyMechanism(),
        new ArgsMechanism(),
        new ConstMechanism(),
        new LiteralMechanism()
    );

    @Override
    List<Mechanism> getMechanisms() {
        return policies;
    }

}
