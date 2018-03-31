package io.osowa.anyfig;

import io.osowa.anyfig.utils.Either;
import io.osowa.anyfig.utils.Pair;
import io.osowa.anyfig.utils.Possible;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

public class Callbacks {

    public final
    Either<
            Pair<
                                Optional<Consumer<Delta>>,
                                Optional<Consumer<Failure>>>,
                Pair<
                    Optional<Method>,
                    Optional<Method>>>
        callbacks;

    public Callbacks(
        Optional<Consumer<Delta>> callback, Optional<Consumer<Failure>> failureCallback,
        Optional<Method> callbackMethod, Optional<Method> failureCallbackMethod)
    {
        Possible<Pair<Optional<Consumer<Delta>>,Optional<Consumer<Failure>>>> callbackPair = Possible.absent();
        if (callback.isPresent() || failureCallback.isPresent()) {
            callbackPair = Possible.of(Pair.of(callback, failureCallback));
        }
        Possible<Pair<Optional<Method>,Optional<Method>>> callbackMethodPair = Possible.absent();
        if (callbackMethod.isPresent() || failureCallbackMethod.isPresent()) {
            callbackMethodPair = Possible.of(Pair.of(callbackMethod, failureCallbackMethod));
        }
        callbacks = Either.or(callbackPair, callbackMethodPair);
    }

}
