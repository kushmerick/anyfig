package io.osowa.anyfig.tests;

import org.junit.Test;

import io.osowa.anyfig.Either;
import io.osowa.anyfig.Possible;

public class EitherTest {

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEither1() {
        Either.or(Possible.of(1), Possible.of(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEither2() {
        Either.or(Possible.absent(), Possible.absent());
    }

}
