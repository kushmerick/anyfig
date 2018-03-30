package io.osowa.anyfig.tests;

import org.junit.Test;

import io.osowa.anyfig.Configurable;
import io.osowa.anyfig.Mechanisms;
import io.osowa.anyfig.Payload;

import java.lang.reflect.Field;
import java.util.Optional;

public class PayloadTest {

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPayload1() throws Exception {
        Field field = TestInvalidPayload.class.getField("staticField");
        TestInvalidPayload object = new TestInvalidPayload();
        new Payload(Optional.of(object), Configurable.DEFAULT, field, Mechanisms.LITERAL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPayload2() throws Exception {
        Field field = TestInvalidPayload.class.getField("field");
        new Payload(Optional.empty(), Configurable.DEFAULT, field, Mechanisms.LITERAL);
    }

    private static class TestInvalidPayload {
        public static int staticField;
        public int field;
    }

}
