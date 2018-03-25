package io.osowa.anyfig;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Configurer {

    private final Anyfig anyfig;
    private final Registrar registrar;
    private final Retriever retriever = new Retriever();
    private final Coercer coercer = new Coercer();

    public Configurer(Anyfig anyfig, Registrar registrar) {
        this.anyfig = anyfig;
        this.registrar = registrar;
    }

    public void configure(String[] args, Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (isConfigurable(field) && Utils.isStatic(field)) {
                configure(args, field);
            }
        }
    }

    public void configure(String[] args, Object object) {
        Optional<Object> oobject = Optional.of(object);
        for (Field field : object.getClass().getDeclaredFields()) {
            if (isConfigurable(field)) {
                if (Utils.isStatic(field)) {
                    // note that we configure static fields encountered when configuring
                    // on object.  this totally seems like the right thing to do! but perhaps
                    // for certain scenarios it will seem weird?!
                    configure(args, field);
                } else {
                    configure(args, oobject, field);
                }
            }
        }
    }

    public void configure(String[] args, Field field) {
        if (!Utils.isStatic(field)) {
            throw new ConfigurationException("Can't configure instance field without an object");
        }
        configure(args, Optional.empty(), field);
    }

    private void configure(String[] args, Optional<Object> object, Field field) {
        if (object.isPresent() == Utils.isStatic(field)) {
            if (object.isPresent()) {
                throw new ConfigurationException("Object supplied for static field");
            } else {
                throw new ConfigurationException("Missing object for instance field");
            }
        }
        Configurable annotation = Utils.getAnnotation(field);
        if (Utils.isStatic(field) && !annotation.blockremote()) {
            anyfig.remoteRegister(field, annotation);
        }
        Optional<Callbacks> callbacks = registrar.getCallbacks(object, field);
        Possible<?> oldVal[] = { Possible.absent() };
        Possible<?> newVal[] = { Possible.absent() };
        boolean eq[] = { false };
        try {
            Utils.whileAccessible(
                field,
                () -> {
                    Object obj = object.orElse(null);
                    oldVal[0] = Possible.of(field.get(obj));
                    newVal[0] = getValue(annotation, field, args);
                    if (newVal[0].present()) {
                        eq[0] = Objects.equals(oldVal[0].get(), newVal[0].get());
                        if (!eq[0]) {
                            field.set(obj, newVal[0].get());
                        }
                    }
                }
            );
            if (newVal[0].present() && !eq[0]) {
                Delta delta = new Delta(object, annotation, field, oldVal[0].get(), newVal[0].get());
                if (callbacks.isPresent()) {
                    invoke(callbacks, delta);
                }
            }
        } catch (Exception exception) {
            try {
                Failure failure = new Failure(object, annotation, field, (Possible<Object>) oldVal[0], (Possible<Object>) newVal[0], exception);
                invoke(callbacks, failure);
            } catch (Exception failureCallbackException) {
                // exception while invoking the failure callback: suppress the original exception, then crash and burn
                ConfigurationException e = new ConfigurationException("Exception while invoking failure callback", failureCallbackException);
                e.addSuppressed(exception);
                throw e;
            }
        }
    }

    private void invoke(Optional<Callbacks> callbacks, Delta delta) throws Exception {
        invoke(callbacks, delta, null);
    }

    private void invoke(Optional<Callbacks> callbacks, Failure failure) throws Exception {
        invoke(callbacks, null, failure);
    }

    private void invoke(Optional<Callbacks> callbacks, Delta delta, Failure failure) throws Exception {
        if (callbacks.isPresent()) {
            Either<Pair<Optional<Consumer<Delta>>,Optional<Consumer<Failure>>>,Pair<Optional<Method>,Optional<Method>>> cbacks = callbacks.get().callbacks;
            if (cbacks.left.present()) {
                Pair<Optional<Consumer<Delta>>,Optional<Consumer<Failure>>> pair = cbacks.left.get();
                if (delta != null) {
                    pair.left.ifPresent(callback -> callback.accept(delta));
                }
                if (failure != null) {
                    pair.right.ifPresent(failureCallback -> failureCallback.accept(failure));
                }
            } else {
                Pair<Optional<Method>,Optional<Method>> pair = cbacks.right.get();
                if (delta != null) {
                    if (pair.left.isPresent()) {
                        invoke(pair.left.get(), delta);
                    }
                }
                if (failure != null) {
                   if (pair.right.isPresent()) {
                       invoke(pair.right.get(), failure);
                   }
                }
            }
        }
    }

    private void invoke(Method method, Object... args) throws Exception {
        if (!Utils.isStatic(method)) {
            throw new ConfigurationException("Cannot invoke instance method callback");
        }
        Utils.whileAccessible(method, () -> {
            try {
                method.invoke(null, args);
            } catch (Exception exception) {
                throw new ConfigurationException("Failure while invoking method `" + method + '`', exception);
            }
        });
    }

    private boolean isConfigurable(Field field) {
        AnnotatedElement[] elements = { field, field.getDeclaringClass(), field.getDeclaringClass().getPackage() };
        // TODO: Above we are ignoring packages marked "ignore".  But when firing
        // TODO: callbacks, we match all descendant packages. So for consistency,
        // TODO: should we check all ancestor packages too?  For example, consider
        // TODO: packages foo.bar that is ignored and foo.bar.baz that is not ignored;
        // TODO: should we ignore some field whose class is in package foo.bar.baz?
        // TODO: I think the intuitive answer is: Yes!
        return
            !Utils.isFinal(field) &&
            Stream.of(elements).allMatch(element ->
                !element.isAnnotationPresent(Configurable.class) ||
                !element.getAnnotation(Configurable.class).ignore());
    }

    private Possible<Object> getValue(Configurable annotation, Field field, String[] args) throws Exception {
        Possible<Object> value = retriever.retrieve(field, annotation, args);
        if (value.present()) {
            Object coerced = coercer.coerce(value.get(), field.getType());
            return Possible.of(coerced);
        } else {
            return Possible.absent();
        }
    }

}
