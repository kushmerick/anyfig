package io.osowa.anyfig.utils;

import io.osowa.anyfig.Anyfig;
import io.osowa.anyfig.Coercer;
import io.osowa.anyfig.Configurable;
import io.osowa.anyfig.Delta;
import io.osowa.anyfig.Failure;
import io.osowa.anyfig.Payload;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

    // logger callbacks

    public static final String ANYFIG = Anyfig.class.getName();
    public static final String UNKNOWN = "<unknown>";
    public static final String REDACTED = "<redacted>";

    private static final Coercer COERCER = new Coercer();

    public static Consumer<Delta> makeLoggerCallback(Logger logger) {
        return delta ->
            logger.info(
                ANYFIG + ": Configured " + loggerContext(delta, delta.oldVal, delta.newVal));
    }

    public static Consumer<Failure> makeLoggerFailureCallback(Logger logger) {
        return failure ->
            logger.warning(
                ANYFIG + ": Failure while configuring " + loggerContext(failure, failure.oldVal.orElse(UNKNOWN), failure.newVal.orElse(UNKNOWN)) +
                ": " + Utils.toString(failure.exception));
    }

    private static String loggerContext(Payload payload, Object oldVal, Object newVal) {
        String context = "class `" + payload.field.getDeclaringClass().getName() + '`';
        if (payload.object.isPresent()) {
            context += " object `" + payload.object.get() + '`';
        }
        context += " field `" + payload.field + "`";
        if (payload.annotation.redact()) {
            oldVal = newVal = REDACTED;
        }
        context += " from `" + oldVal + "` to `" + newVal + '`';
        return context;
    }

    private static String toString(Throwable throwable) {
        String result = throwable.toString() + ": " + Arrays.asList(throwable.getStackTrace());
        Throwable cause = throwable.getCause();
        boolean c = cause != null;
        Throwable[] suppressed = throwable.getSuppressed();
        boolean s = suppressed.length > 0;
        if (c || s) {
            result += " (";
        }
        if (c) {
            result += "cause: " + toString(throwable.getCause());
        }
        if (c && s) {
            result += "; ";
        }
        if (s) {
            result += "suppressed: " + Stream.of(suppressed).map(Utils::toString).collect(Collectors.toList());
        }
        if (c || s) {
            result += ')';
        }
        return result;
    }

    // fool-proof execution of some action while an object is accessible and then reset accessibility even if
    // the action throws an exception; properly propagates security exceptions while modifying accessibility.

    public static <T> T whileAccessible(AccessibleObject object, Callable<T> action) throws Exception {
        boolean accessible = false;
        Exception actionException = null;
        try {
            object.setAccessible(true);
            accessible = true;
            return action.call();
        } catch (Exception exception) {
            if (accessible) {
                // we set the object to be accessible, but the action threw an exception; grab it so we can attach
                // a suppressed exception below in the unlikely event that we can't reset the object's accessibility
                actionException = exception;
            } else {
                // we failed to set the object to be accessible; nothing more to do...
            }
            // either way, we want to propagate the exception
            throw exception;
        } finally {
            if (accessible) {
                // we set the object to be accessible, so reset it
                try {
                    object.setAccessible(false);
                } catch (Exception exception) {
                    // hmmm... wierd... we were able to set the object to be accessible, but not reset it -- seems like a JVM bug?!!?
                    if (actionException == null) {
                        // we successfully invoked the action, but then can't reset the object's accessibility.
                        // hmm... darn... what to do? on the one hand, it's sad to discard the action's result,
                        // not to mention that the action may have had side effects which the client must unwind
                        // if we throw an exception; on the other hand, this is a very strange situation that
                        // should never occur, so let's find out for sure!
                        throw exception;
                    } else {
                        // we were able to set the object to be accessible, but the action threw an exception
                        // (which is in flight as we pass through this `finally` clause), so mark this clean-up
                        // exception as suppressed by the action's exception
                        actionException.addSuppressed(exception);
                    }
                }
            } else {
                // we were unable to set the object to be accessible, so no need to reset it
            }
        }
    }

    public static boolean isFinal(Member member) {
        return Modifier.isFinal(member.getModifiers());
    }

    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    public interface FallibleRunnable {
        void run() throws Exception;
    }

    public static void whileAccessible(AccessibleObject object, FallibleRunnable action) throws Exception {
        whileAccessible(
            object,
            () -> {
                action.run();
                return null;
            });
    }

    public static String encodeField(Field field) {
        return
            field.getDeclaringClass().getName() +
            '.' +
            field.getName();
    }

    public static Pair<String,String> decodeFieldKey(String key) {
        int lastdot = key.lastIndexOf('.');
        return Pair.of(key.substring(0, lastdot), key.substring(lastdot+1));
    }

    public static Object getField(Field field) throws Exception {
        return whileAccessible(field, () -> field.get(null));
    }

    public static void setField(Field field, Object value) throws Exception {
        whileAccessible(field, () -> field.set(null, COERCER.coerce(value, field.getType())));
    }

    public static Configurable getAnnotation(Field field) {
        return field.isAnnotationPresent(Configurable.class)
            ? field.getAnnotation(Configurable.class)
            : Configurable.DEFAULT;
    }

}
