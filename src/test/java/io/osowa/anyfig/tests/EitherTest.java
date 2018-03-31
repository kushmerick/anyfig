package io.osowa.anyfig.tests;

import org.junit.Test;

import io.osowa.anyfig.utils.Either;
import io.osowa.anyfig.utils.Possible;

public class EitherTest {

    @Test
    public void testValidEither() {
        Either.or(Possible.of(1), Possible.absent());
        Either.or(Possible.absent(), Possible.of(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEither1() {
        Either.or(Possible.of(1), Possible.of(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEither2() {
        Either.or(Possible.absent(), Possible.absent());
    }

}
