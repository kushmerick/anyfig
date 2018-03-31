package io.osowa.anyfig;

import io.osowa.anyfig.api.RemoteAPI;
import io.osowa.anyfig.api.xe.XeRemoteAPI;
import io.osowa.anyfig.utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;

// the universal configurer for anything & everything.

public class Anyfig implements AutoCloseable {

    private final Registrar registrar = new Registrar();
    private final History history = new History();
    private final Configurer configurer = new Configurer(this, registrar, history);
    private final RemoteAPI remoteapi = new XeRemoteAPI();

    // A: register callbacks: five targets (fields, objects, classes, packages, global);
    // three kinds (Consumer<Delta>/Consumer<Failure>/BiConsumer<Delta,Failure>, Method, Logger);
    // and with and without arguments

    // // target: fields

    public void register(Consumer<Delta> callback, Field... fields) throws ConfigurationException {
        register(callback, failure -> {}, fields);
    }

    public void register(BiConsumer<Delta,Failure> callback, Field... fields) throws ConfigurationException {
        register(delta -> callback.accept(delta,null), failure -> callback.accept(null,failure), fields);
    }

    public void register(Consumer<Delta> callback, Consumer<Failure> failureCallback, Field... fields) throws ConfigurationException {
        register(Optional.ofNullable(callback), Optional.ofNullable(failureCallback), Optional.empty(), Optional.empty(), fields);
    }

    public void register(Method callback, Field... fields) {
        register(callback, null, fields);
    }

    public void register(Method callback, Method failureCallback, Field... fields) {
        register(Optional.empty(), Optional.empty(), Optional.ofNullable(callback), Optional.ofNullable(failureCallback), fields);
    }

    public void register(Logger logger, Field... fields) {
        register(Utils.makeLoggerCallback(logger), logger, fields);
    }

    public void register(Consumer<Delta> callback, Logger failureLogger, Field... fields) {
        register(callback, Utils.makeLoggerFailureCallback(failureLogger), fields);
    }

    private void register(
            Optional<Consumer<Delta>> callback, Optional<Consumer<Failure>> failureCallback,
            Optional<Method> callbackMethod, Optional<Method> failureCallbackMethod,
            Field... fields)
    {

        Callbacks callbacks = new Callbacks(callback, failureCallback, callbackMethod, failureCallbackMethod);
        Stream.of(fields).forEach(field -> registrar.register(callbacks, field));
    }

    // // target: objects

    public void register(Consumer<Delta> callback, Object... objects) throws ConfigurationException {
        register(callback, failure -> {}, objects);
    }

    public void register(BiConsumer<Delta,Failure> callback, Object... objects) throws ConfigurationException {
        register(delta -> callback.accept(delta,null), failure -> callback.accept(null,failure), objects);
    }

    public void register(Consumer<Delta> callback, Consumer<Failure> failureCallback, Object... objects) throws ConfigurationException {
        register(Optional.ofNullable(callback), Optional.ofNullable(failureCallback), Optional.empty(), Optional.empty(), objects);
    }

    public void register(Method callback, Object... objects) {
        register(callback, null, objects);
    }

    public void register(Method callback, Method failureCallback, Object... objects) {
        register(Optional.empty(), Optional.empty(), Optional.ofNullable(callback), Optional.ofNullable(failureCallback), objects);
    }

    public void register(Logger logger, Object... objects) {
        register(Utils.makeLoggerCallback(logger), logger, objects);
    }

    public void register(Consumer<Delta> callback, Logger failureLogger, Object... objects) {
        register(callback, Utils.makeLoggerFailureCallback(failureLogger), objects);
    }

    private void register(
            Optional<Consumer<Delta>> callback, Optional<Consumer<Failure>> failureCallback,
            Optional<Method> callbackMethod, Optional<Method> failureCallbackMethod,
            Object... objects)
    {
        Callbacks callbacks = new Callbacks(callback, failureCallback, callbackMethod, failureCallbackMethod);
        Stream.of(objects).forEach(object -> registrar.register(callbacks, object));
    }

    // // target: classes

    public void register(Consumer<Delta> callback, Class<?>... classes) throws ConfigurationException {
        register(callback, failure -> {}, classes);
    }

    public void register(BiConsumer<Delta,Failure> callback, Class<?>... classes) throws ConfigurationException {
        register(delta -> callback.accept(delta,null), failure -> callback.accept(null,failure), classes);
    }

    public void register(Consumer<Delta> callback, Consumer<Failure> failureCallback, Class<?>... classes) throws ConfigurationException {
        register(Optional.ofNullable(callback), Optional.ofNullable(failureCallback), Optional.empty(), Optional.empty(), classes);
    }

    public void register(Method callback, Class<?>... classes) {
        register(callback, null, classes);
    }

    public void register(Method callback, Method failureCallback, Class<?>... classes) {
        register(Optional.empty(), Optional.empty(), Optional.ofNullable(callback), Optional.ofNullable(failureCallback), classes);
    }

    public void register(Logger logger, Class<?>... classes) {
        register(Utils.makeLoggerCallback(logger), logger, classes);
    }

    public void register(Consumer<Delta> callback, Logger failureLogger, Class<?>... classes) {
        register(callback, Utils.makeLoggerFailureCallback(failureLogger), classes);
    }

    private void register(
        Optional<Consumer<Delta>> callback, Optional<Consumer<Failure>> failureCallback,
        Optional<Method> callbackMethod, Optional<Method> failureCallbackMethod,
        Class<?>... classes)
    {
        Callbacks callbacks = new Callbacks(callback, failureCallback, callbackMethod, failureCallbackMethod);
        Stream.of(classes).forEach(clazz -> registrar.register(callbacks, clazz));
    }

    // // target: packages

    public void register(Consumer<Delta> callback, Package... packages) throws ConfigurationException {
        register(callback, failure -> {}, packages);
    }

    public void register(BiConsumer<Delta,Failure> callback, Package... packages) throws ConfigurationException {
        register(delta -> callback.accept(delta,null), failure -> callback.accept(null,failure), packages);
    }

    public void register(Consumer<Delta> callback, Consumer<Failure> failureCallback, Package... packages) throws ConfigurationException {
        register(Optional.ofNullable(callback), Optional.ofNullable(failureCallback), Optional.empty(), Optional.empty(), packages);
    }

    public void register(Method callback, Package... packages) {
        register(callback, null, packages);
    }

    public void register(Method callback, Method failureCallback, Package... packages) {
        register(Optional.empty(), Optional.empty(), Optional.ofNullable(callback), Optional.ofNullable(failureCallback), packages);
    }

    public void register(Logger logger, Package... packages) {
        register(Utils.makeLoggerCallback(logger), logger, packages);
    }

    public void register(Consumer<Delta> callback, Logger failureLogger, Package... packages) {
        register(callback, Utils.makeLoggerFailureCallback(failureLogger), packages);
    }

    private void register(
            Optional<Consumer<Delta>> callback, Optional<Consumer<Failure>> failureCallback,
            Optional<Method> callbackMethod, Optional<Method> failureCallbackMethod,
            Package... packages)
    {
        Callbacks callbacks = new Callbacks(callback, failureCallback, callbackMethod, failureCallbackMethod);
        Stream.of(packages).forEach(pkg -> registrar.register(callbacks, pkg));
    }

    // // target: global

    public void register(Consumer<Delta> callback) throws ConfigurationException {
        register(callback, failure -> {});
    }

    public void register(BiConsumer<Delta,Failure> callback) throws ConfigurationException {
        register(delta -> callback.accept(delta,null), failure -> callback.accept(null,failure));
    }

    public void register(Consumer<Delta> callback, Consumer<Failure> failureCallback) throws ConfigurationException {
        register(Optional.ofNullable(callback), Optional.ofNullable(failureCallback), Optional.empty(), Optional.empty());
    }

    public void register(Method callback) {
        register(callback, (Method) null);
    }

    public void register(Method callback, Method failureCallback) {
        register(Optional.empty(), Optional.empty(), Optional.ofNullable(callback), Optional.ofNullable(failureCallback));
    }

    public void register(Logger logger) {
        register(Utils.makeLoggerCallback(logger), logger);
    }

    public void register(Consumer<Delta> callback, Logger failureLogger) {
        register(callback, Utils.makeLoggerFailureCallback(failureLogger));
    }

    private void register(
            Optional<Consumer<Delta>> callback, Optional<Consumer<Failure>> failureCallback,
            Optional<Method> callbackMethod, Optional<Method> failureCallbackMethod)
    {
        registrar.register(new Callbacks(callback, failureCallback, callbackMethod, failureCallbackMethod));
    }

    // B. configure objects with the currently registered callbacks

    private static final String[] EMPTY_ARGS = {};

    public void configure(Class<?>... classes) {
        configure(EMPTY_ARGS, classes);
    }

    public void configure(String[] args, Class<?>... classes) {
        Stream.of(classes).forEach(clazz -> configurer.configure(args, clazz));
    }

    public void configure(Object... objects) {
        configure(EMPTY_ARGS, objects);
    }

    public void configure(String[] args, Object... objects) {
        Stream.of(objects).forEach(object -> configurer.configure(args, object));
    }

    public void configure(Field... fields) {
        configure(EMPTY_ARGS, fields);
    }

    public void configure(String[] args, Field... fields) {
        Stream.of(fields).forEach(field -> configurer.configure(args, field));
    }

    // C. helpful shortcut: register additional callbacks and configure the given object/field/class

    // // target: fields

    public void configure(Consumer<Delta> callback, Field... fields) throws ConfigurationException {
        configure(callback, EMPTY_ARGS, fields);
    }

    public void configure(BiConsumer<Delta,Failure> callback, Field... fields) throws ConfigurationException {
        configure(callback, EMPTY_ARGS, fields);
    }

    public void configure(Consumer<Delta> callback, Consumer<Failure> failureCallback, Field... fields) throws ConfigurationException {
        configure(callback, failureCallback, EMPTY_ARGS, fields);
    }

    public void configure(Method callback, Field... fields) {
        configure(callback, EMPTY_ARGS, fields);
    }

    public void configure(Method callback, Method failureCallback, Field... fields) {
        configure(callback, failureCallback, EMPTY_ARGS, fields);
    }

    public void configure(Logger logger, Field... fields) {
        configure(logger, EMPTY_ARGS, fields);
    }

    public void configure(Consumer<Delta> callback, Logger failureLogger, Field... fields) {
        configure(callback, failureLogger, EMPTY_ARGS, fields);
    }

    public void configure(Consumer<Delta> callback, String[] args, Field... fields) throws ConfigurationException {
        register(callback, fields);
        configure(args, fields);
    }

    public void configure(BiConsumer<Delta,Failure> callback, String[] args, Field... fields) throws ConfigurationException {
        register(callback, fields);
        configure(args, fields);
    }

    public void configure(Consumer<Delta> callback, Consumer<Failure> failureCallback, String[] args, Field... fields) throws ConfigurationException {
        register(callback, failureCallback, fields);
        configure(args, fields);
    }

    public void configure(Method callback, String[] args, Field... fields) {
        register(callback, fields);
        configure(args, fields);
    }

    public void configure(Method callback, Method failureCallback, String[] args, Field... fields) {
        register(callback, failureCallback, fields);
        configure(args, fields);
    }

    public void configure(Logger logger, String[] args, Field... fields) {
        register(logger, fields);
        configure(args, fields);
    }

    public void configure(Consumer<Delta> callback, Logger failureLogger, String[] args, Field... fields) {
        register(callback, failureLogger, fields);
        configure(args, fields);
    }

    // // target: objects

    public void configure(Consumer<Delta> callback, Object... objects) throws ConfigurationException {
        configure(callback, EMPTY_ARGS, objects);
    }

    public void configure(BiConsumer<Delta,Failure> callback, Object... objects) throws ConfigurationException {
        configure(callback, EMPTY_ARGS, objects);
    }

    public void configure(Consumer<Delta> callback, Consumer<Failure> failureCallback, Object... objects) throws ConfigurationException {
        configure(callback, failureCallback, EMPTY_ARGS, objects);
    }

    public void configure(Method callback, Object... objects) {
        configure(callback, EMPTY_ARGS, objects);
    }

    public void configure(Method callback, Method failureCallback, Object... objects) {
        configure(callback, failureCallback, EMPTY_ARGS, objects);
    }

    public void configure(Logger logger, Object... objects) {
        configure(logger, EMPTY_ARGS, objects);
    }

    public void configure(Consumer<Delta> callback, Logger failureLogger, Object... objects) {
        configure(callback, failureLogger, EMPTY_ARGS, objects);
    }

    public void configure(Consumer<Delta> callback, String[] args, Object... objects) throws ConfigurationException {
        register(callback, objects);
        configure(args, objects);
    }

    public void configure(BiConsumer<Delta,Failure> callback, String[] args, Object... objects) throws ConfigurationException {
        register(callback, objects);
        configure(args, objects);
    }

    public void configure(Consumer<Delta> callback, Consumer<Failure> failureCallback, String[] args, Object... objects) throws ConfigurationException {
        register(callback, failureCallback, objects);
        configure(args, objects);
    }

    public void configure(Method callback, String[] args, Object... objects) {
        register(callback, objects);
        configure(args, objects);
    }

    public void configure(Method callback, Method failureCallback, String[] args, Object... objects) {
        register(callback, failureCallback, objects);
        configure(args, objects);
    }

    public void configure(Logger logger, String[] args, Object... objects) {
        register(logger, objects);
        configure(args, objects);
    }

    public void configure(Consumer<Delta> callback, Logger failureLogger, String[] args, Object... objects) {
        register(callback, failureLogger, objects);
        configure(args, objects);
    }

    // // target: classes

    public void configure(Consumer<Delta> callback, Class<?>... classes) throws ConfigurationException {
        configure(callback, EMPTY_ARGS, classes);
    }

    public void configure(BiConsumer<Delta,Failure> callback, Class<?>... classes) throws ConfigurationException {
        configure(callback, EMPTY_ARGS, classes);
    }

    public void configure(Consumer<Delta> callback, Consumer<Failure> failureCallback, Class<?>... classes) throws ConfigurationException {
        configure(callback, failureCallback, EMPTY_ARGS, classes);
    }

    public void configure(Method callback, Class<?>... classes) {
        configure(callback, EMPTY_ARGS, classes);
    }

    public void configure(Method callback, Method failureCallback, Class<?>... classes) {
        configure(callback, failureCallback, EMPTY_ARGS, classes);
    }

    public void configure(Logger logger, Class<?>... classes) {
        configure(logger, EMPTY_ARGS, classes);
    }

    public void configure(Consumer<Delta> callback, Logger failureLogger, Class<?>... classes) {
        configure(callback, failureLogger, EMPTY_ARGS, classes);
    }

    public void configure(Consumer<Delta> callback, String[] args, Class<?>... classes) throws ConfigurationException {
        register(callback, classes);
        configure(args, classes);
    }

    public void configure(BiConsumer<Delta,Failure> callback, String[] args, Class<?>... classes) throws ConfigurationException {
        register(callback, classes);
        configure(args, classes);
    }

    public void configure(Consumer<Delta> callback, Consumer<Failure> failureCallback, String[] args, Class<?>... classes) throws ConfigurationException {
        register(callback, failureCallback, classes);
        configure(args, classes);
    }

    public void configure(Method callback, String[] args, Class<?>... classes) {
        register(callback, classes);
        configure(args, classes);
    }

    public void configure(Method callback, Method failureCallback, String[] args, Class<?>... classes) {
        register(callback, failureCallback, classes);
        configure(args, classes);
    }

    public void configure(Logger logger, String[] args, Class<?>... classes) {
        register(logger, classes);
        configure(args, classes);
    }

    public void configure(Consumer<Delta> callback, Logger failureLogger, String[] args, Class<?>... classes) {
        register(callback, failureLogger, classes);
        configure(args, classes);
    }

    // configuring the Remote API

    /**
     * Enable Remote API with default configuration, overridden by the following properties or
     * environment variables:
     * - anyfig.remote-api.level / ANYFIG_REMOTE_API_LEVEL
     * - anyfig.remote-api.bindAddress / ANYFIG_REMOTE_API_BIND_ADDRESS
     * - anyfig.remote-api.port / ANYFIG_REMOTE_API_PORT
     * - anyfig.remote-api.dataDir / ANYFIG_REMOTE_API_DATA_DIR
     * - anyfig.remote-api.peers / ANYFIG_REMOTE_API_PEERS
     * - anyfig.remote-api.token / ANYFIG_REMOTE_API_TOKEB
     */
    public RemoteAPI.Config enableRemoteAPI() {
        return enableRemoteAPI(new String[0]);
    }

    /**
     * Enable Remote API with default configuration, overridden by the following properties,
     * environment variables, or arguments:
     * - anyfig.remote-api.level / ANYFIG_REMOTE_API_LEVEL / --anyfigRemoteAPILevel
     * - anyfig.remote-api.bindAddress / ANYFIG_REMOTE_API_BIND_ADDRESS / --anyfigRemoteAPIBindAddress
     * - anyfig.remote-api.port / ANYFIG_REMOTE_API_PORT / --anyfigRemoteAPIPort
     * - anyfig.remote-api.dataDir / ANYFIG_REMOTE_API_DATA_DIR / --anyfigRemoteAPIDataDir
     * - anyfig.remote-api.peers / ANYFIG_REMOTE_API_PEERS / --anyfigRemoteAPIPeers
     * - anyfig.remote-api.token / ANYFIG_REMOTE_API_TOKEB / --anyfigRemoteAPIToken
     */
    public RemoteAPI.Config enableRemoteAPI(String[] args) {
        // dogfood Anyfig to pick up REST API configuration; but note that we create our
        // own nested Anyfig so there's no danger of invoking the client's callbacks
        Anyfig anyfig = new Anyfig();
        RemoteAPI.Config config = new RemoteAPI.Config();
        anyfig.configure(args, config);
        return enableRemoteAPI(config);
    }

    /**
     * Enable Remote API with the given configuration.
     * @param config
     */
    public RemoteAPI.Config enableRemoteAPI(RemoteAPI.Config config) {
        disableRESTAPI(); // just in case...
        remoteapi.start(config, this);
        return config;
    }

    /**
     * Disable the Remote API.
     */
    public void disableRESTAPI() {
        stop();
    }

    private void stop() {
        remoteapi.stop();
    }

    @Override
    public void close() throws Exception {
        stop();
    }

    // direct manipulation of values by REST keys; intended for
    // use by the REST API, but... have at it... knock yourself out...

    // register the given field for the Remote API
    public void remoteRegister(Field field, Configurable annotation) {
        registrar.registerRemote(field, annotation);
    }

    // enumerate the keys that are registered with from the Remote API
    public Set<String> remoteEnumerate() {
        return registrar.enumerateRemote();
    }

    // get the field that is associated with the given remote key (or Optional.absent() if the key is invalid)
    public Optional<Field> getRemoteKey(String key) {
        return registrar.getRemoteKey(key);
    }

    // set an object as requested to do so by the Remote API
    public void remoteSet(Field field, Object value) throws Exception {
        Utils.setField(field, value);
    }

    // history

    public List<Delta> getHistory() {
        return Collections.unmodifiableList(history.get());
    }

}
