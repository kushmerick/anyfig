package io.osowa.anyfig;

import com.google.common.base.Strings;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Registrar {

    public Callbacks globalCallbacks = null;
    public final Map<String,Callbacks> packageCallbacks = new HashMap<>();
    public final Map<Class,Callbacks> classCallbacks = new HashMap<>();
    public final Map<Object,Callbacks> objectCallbacks = new HashMap<>();
    public final Map<Field,Callbacks> fieldCallbacks = new HashMap<>();

    public final Map<String,Field> remoteKeys = new HashMap<>();

    public synchronized void register(Callbacks callbacks) {
        globalCallbacks = callbacks;
    }

    public synchronized void register(Callbacks callbacks, Package pkg) {
        packageCallbacks.put(pkg.getName(), callbacks);
    }

    public synchronized void register(Callbacks callbacks, Class<?> clazz) {
        classCallbacks.put(clazz, callbacks);
    }

    public synchronized void register(Callbacks callbacks, Object object) {
        objectCallbacks.put(object, callbacks);
    }

    public synchronized void register(Callbacks callbacks, Field field) {
        fieldCallbacks.put(field, callbacks);
    }

    public synchronized Optional<Callbacks> getCallbacks(Optional<Object> object, Field field) {
        if (fieldCallbacks.containsKey(field)) {
            return Optional.of(fieldCallbacks.get(field));
        }
        if (object.isPresent() && objectCallbacks.containsKey(object.get())) {
            return Optional.of(objectCallbacks.get(object.get()));
        }
        Class<?> clazz = field.getDeclaringClass();
        if (classCallbacks.containsKey(clazz)) {
            return Optional.of(classCallbacks.get(field.getDeclaringClass()));
        }
        Package pkg = clazz.getPackage();
        if (pkg != null) {
            // package foo.bar.baz: check (foo.bar.baz, foo.bar, foo) in that order up to the root
            String name = pkg.getName();
            while (true) {
                if (packageCallbacks.containsKey(name)) {
                    return Optional.of(packageCallbacks.get(name));
                }
                int lastdot = name.lastIndexOf('.');
                if (lastdot == -1) break; // we've reached the root
                name = name.substring(0, lastdot);
            }
        }
        if (globalCallbacks != null) {
            return Optional.of(globalCallbacks);
        }
        return Optional.empty();
    }

    public synchronized void registerRemote(Field field, Configurable annotation) {
        String key = annotation.remote();
        if (key.isEmpty()) {
            key = Utils.encodeField(field);
        }
        remoteKeys.put(key, field);
    }

    public synchronized Set<String> enumerateRemote() {
        return remoteKeys.keySet();
    }

    public synchronized Optional<Field> getRemoteKey(String key) {
        return Optional.ofNullable(remoteKeys.get(key));
    }

}
