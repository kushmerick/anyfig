package io.osowa.anyfig.tests;

import com.google.gson.Gson;

import org.junit.Test;

import io.osowa.anyfig.Configurable;
import io.osowa.anyfig.Delta;
import io.osowa.anyfig.Failure;
import io.osowa.anyfig.Mechanisms;
import io.osowa.anyfig.Possible;
import io.osowa.anyfig.Utils;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UtilsTest {

    @Test
    public void testLoggerCallbacksAndExceptionToString() throws Exception {
        String[] info = { null }, warn = { null };
        Logger logger = new Logger(null, null) {
            @Override public void info(String msg) {
                info[0] = msg;
            }
            @Override public void warning(String msg) {
                warn[0] = msg;
            }
        };
        Consumer<Delta> callback = Utils.makeLoggerCallback(logger);
        Consumer<Failure> failureCallback = Utils.makeLoggerFailureCallback(logger);
        Delta delta = new Delta(
            Optional.of(new TestLoggerCallbacksAndExceptionToString(1)),
            Configurable.DEFAULT,
            TestLoggerCallbacksAndExceptionToString.class.getField("field"),
            Mechanisms.LITERAL,
            new TestLoggerCallbacksAndExceptionToString(2),
            new TestLoggerCallbacksAndExceptionToString(3));
        callback.accept(delta);
        assertEquals(
            "io.osowa.anyfig.Anyfig: Configured class `io.osowa.anyfig.tests.UtilsTest$TestLoggerCallbacksAndExceptionToString` object `{\"field\":1}` field `public int io.osowa.anyfig.tests.UtilsTest$TestLoggerCallbacksAndExceptionToString.field` from `{\"field\":2}` to `{\"field\":3}`",
            info[0]);
        Exception exception = new RuntimeException("Foo", new RuntimeException("Bar"));
        exception.addSuppressed(new RuntimeException("Baz"));
        Failure failure = new Failure(
            Optional.of(new TestLoggerCallbacksAndExceptionToString(1)),
            Configurable.DEFAULT,
            TestLoggerCallbacksAndExceptionToString.class.getField("field"),
            Mechanisms.LITERAL,
            Possible.absent(),
            Possible.absent(),
            exception);
        failureCallback.accept(failure);
        assertTrue(
            warn[0]
            .startsWith("io.osowa.anyfig.Anyfig: Failure while configuring class `io.osowa.anyfig.tests.UtilsTest$TestLoggerCallbacksAndExceptionToString` object `{\"field\":1}` field `public int io.osowa.anyfig.tests.UtilsTest$TestLoggerCallbacksAndExceptionToString.field` from `<unknown>` to `<unknown>`: java.lang.RuntimeException: Foo: [io.osowa.anyfig.tests.UtilsTest.testLoggerCallbacksAndExceptionToString(UtilsTest.java:"));
        assertTrue(warn[0].contains("cause: java.lang.RuntimeException: Bar"));
        assertTrue(warn[0].contains("suppressed: [java.lang.RuntimeException: Baz"));
    }

    @Test
    public void testLoggerCallbacksRedactFields() throws Exception {
        String[] info = { null }, warn = { null };
        Logger logger = new Logger(null, null) {
            @Override public void info(String msg) {
                info[0] = msg;
            }
            @Override public void warning(String msg) {
                warn[0] = msg;
            }
        };
        Consumer<Delta> callback = Utils.makeLoggerCallback(logger);
        Consumer<Failure> failureCallback = Utils.makeLoggerFailureCallback(logger);
        Configurable redacted = new Configurable() {
            public Class<? extends Annotation> annotationType() {
                return Configurable.class;
            }
            @Override public boolean ignore() {
                return false;
            }
            @Override public boolean literal() {
                return false;
            }
            @Override public boolean NULL() {
                return false;
            }
            @Override public String value() {
                return null;
            }
            @Override public String constant() {
                return null;
            }
            @Override public String argument() {
                return null;
            }
            @Override public String property() {
                return null;
            }
            @Override public String envvar() {
                return null;
            }
            @Override public String remote() {
                return null;
            }
            @Override public boolean blockremote() {
                return false;
            }
            @Override public boolean redact() {
                return true; // this is what we're testing
            }
        };
        int secret1 = 888888;
        int secret2 = 999999;
        Delta delta = new Delta(
            Optional.of(new TestLoggerCallbacksAndExceptionToString(1)),
            redacted,
            TestLoggerCallbacksAndExceptionToString.class.getField("field"),
            Mechanisms.LITERAL,
            new TestLoggerCallbacksAndExceptionToString(secret1),
            new TestLoggerCallbacksAndExceptionToString(secret2));
        callback.accept(delta);
        assertFalse(info[0].contains(String.valueOf(secret1)));
        assertFalse(info[0].contains(String.valueOf(secret2)));
        String redacted2redacted = '`' + Utils.REDACTED + "` to `" + Utils.REDACTED + '`';
        assertTrue(info[0].contains(redacted2redacted));
        Exception exception = new RuntimeException();
        Failure failure = new Failure(
            Optional.of(new TestLoggerCallbacksAndExceptionToString(1)),
            redacted,
            TestLoggerCallbacksAndExceptionToString.class.getField("field"),
            Mechanisms.LITERAL,
            Possible.absent(),
            Possible.absent(),
            exception);
        failureCallback.accept(failure);
        assertFalse(warn[0].contains(String.valueOf(secret1)));
        assertFalse(warn[0].contains(String.valueOf(secret2)));
        assertTrue(warn[0].contains(redacted2redacted));
    }

    private static class TestLoggerCallbacksAndExceptionToString {
        private static final Gson GSON = new Gson();
        public String toString() {
            return GSON.toJson(this);
        }
        public int field;
        public TestLoggerCallbacksAndExceptionToString(int field) {
            this.field = field;
        }
    }

}
