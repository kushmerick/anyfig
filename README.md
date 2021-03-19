# Anyfig

Anyfig is a flexible Java configuration utility.

[![Build Status](https://travis-ci.org/kushmerick/anyfig.svg?branch=master)](https://travis-ci.org/kushmerick/anyfig)
[![Coverage Status](https://coveralls.io/repos/github/kushmerick/anyfig/badge.svg?branch=master)](https://coveralls.io/github/kushmerick/anyfig?branch=master)

# Overview

Anyfig supports:

* configuring a class's static fields, or an object's instance fields;
* retrieving values from literals, other fields, properties, environment variables, and command line arguments;
  the retrieved value can be anything: strings, wrapped or primitive numbers/booleans/characters, enums, or arbitrary
  objects serialized as JSON;
* customizing its behavior with the
  [@Configurable](http://github.com/kushmerick/anyfig/tree/master/src/main/java/io/osowa/anyfig/Configurable.java)
  annotation -- but it's optional with sensible defaults;
* intuitively coercing between different data types;
* a Remote API for enumerating and setting configurable fields;
* callbacks that are invoked when fields are modified (including failure callbacks in case something goes wrong);
  callbacks can be arbitrary Consumer or static Methods, or a Logger to which informative messages should be sent;
  callbacks can be registered individually on objects, fields, classes or packages;
* configuring any number of classes or objects, with either shared or specialized callbacks -- whatever is convenient
  for your application;
* inspecting the history of when changes occurred.

The [AnyfigTest](http://github.com/kushmerick/anyfig/tree/master/src/test/java/io/osowa/anyfig/tests/AnyfigTest.java)
and [RemoteAPITest](http://github.com/kushmerick/anyfig/tree/master/src/test/java/io/osowa/anyfig/tests/RemoteAPITest.java)
unit tests can serve as a detailed tutorial that demonstrates Anyfig's various capabilities.

## Example

Here's a simple [Example](http://github.com/kushmerick/anyfig/tree/master/src/main/java/io/osowa/anyfig/examples/Example.java).

First, let's define a class that holds three configurable fields:

     class Settings {
         static int maxVehicles;
         static double minSpeed;
         enum Mode { LEFT, RIGHT };
         private static final Mode DEFAULT_MODE = Mode.LEFT;
         static Mode mode;
     }

Second, we'll define a callback that Anyfig will invoke as fields are configured:

     static void callback(Delta delta) {
         System.out.println("Configuring `" + delta.field + "` to `" + delta.newVal + '`');
     }

Third, let's tell Anyfig to configure our fields:

     public static void main(String[] args) {
         Anyfig anyfig = new Anyfig();
         anyfig.configure(Example::callback, args, Settings.class);
     }

Let's run our program:

    $ java -jar anyfig-1.0-SNAPSHOT-jar-with-dependencies.jar
    Configuring `static io.osowa.anyfig.examples.Example$Settings$Mode io.osowa.anyfig.examples.Example$Settings.mode` to `LEFT`

This tiny examples shows that Anyfig doesn't touch `maxVehicles` or `minSpeed`, but it configures `mode` to `LEFT`. Here
we're seeing just one of Anyfig's several mechanisms: Anyfig can configure a field named `fooBar` from a constant named
`DEFAULT_FOO_BAR`.

Let's run the program again, this time supplying custom configuration with command-line arguments:

    $ java -jar anyfig-1.0-SNAPSHOT-jar-with-dependencies.jar --mode=RIGHT --minSpeed=10
    Configuring `static double io.osowa.anyfig.examples.Example$Settings.minSpeed` to `10.0`
    Configuring `static io.osowa.anyfig.examples.Example$Settings$Mode io.osowa.anyfig.examples.Example$Settings.mode` to `RIGHT`

Do you prefer environment-variables instead of command-line arguments?  Anyfig supports that too:

    $ MODE=RIGHT MIN_SPEED=10 java -jar anyfig-1.0-SNAPSHOT-jar-with-dependencies.jar
    Configuring `static double io.osowa.anyfig.examples.Example$Settings.minSpeed` to `10.0`
    Configuring `static io.osowa.anyfig.examples.Example$Settings$Mode io.osowa.anyfig.examples.Example$Settings.mode` to `RIGHT`

Anyfig also supports Java properties:

    $ java -DmaxVehicles=100 -DminSpeed=10 -Dmode=RIGHT -jar anyfig-1.0-SNAPSHOT-jar-with-dependencies.jar
    Configuring `static int io.osowa.anyfig.examples.Example$Settings.maxVehicles` to `100`
    Configuring `static double io.osowa.anyfig.examples.Example$Settings.minSpeed` to `10.0`
    Configuring `static io.osowa.anyfig.examples.Example$Settings$Mode io.osowa.anyfig.examples.Example$Settings.mode` to `RIGHT`

Of course you can use a mixtures of different methods:

    $ MODE=RIGHT java -DmaxVehicles=100 -jar anyfig-1.0-SNAPSHOT-jar-with-dependencies.jar --minSpeed=10
    Configuring `static int io.osowa.anyfig.examples.Example$Settings.maxVehicles` to `100`
    Configuring `static double io.osowa.anyfig.examples.Example$Settings.minSpeed` to `10.0`
    Configuring `static io.osowa.anyfig.examples.Example$Settings$Mode io.osowa.anyfig.examples.Example$Settings.mode` to `RIGHT`

Anyfig also supports remote configuration with an optional tiny embedded HTTP server.  The Remote API is
disabled by default, so let's enable it:

    public static void main(String[] args) {
        Anyfig anyfig = new Anyfig();
        anyfig.configure(Example::callback, args, Settings.class);
        anyfig.enableRemoteAPI();
        (new CountDownLatch(1)).await();
    }

Now let's try it out:

    $ ANYFIG_REMOTE_API_TOKEN=s3kret java -jar anyfig-1.0-SNAPSHOT-jar-with-dependencies.jar
    Setting `io.osowa.anyfig.examples.Example$Settings$Mode io.osowa.anyfig.examples.Example$Settings.mode` to `LEFT`

Note that for security, we specify an authorization token which must be provided on all requests.

Our program is waiting for us to talk to it via the Remote API.  Let's first enumerate all configurable
fields:

    $ curl -H 'Authorization: s3kret' http://localhost:9111/anyfig
    { "values": {
        "io.osowa.anyfig.examples.Example$Settings.minSpeed": 0.0,
        "io.osowa.anyfig.examples.Example$Settings.maxVehicles": 0,
        "io.osowa.anyfig.examples.Example$Settings.mode": "LEFT" } }

We can also configure fields using this API:

    $ curl -H 'Authorization: s3kret' -X PATCH -d '{"value":10}' -H 'Content-Type: application/json' 'http://localhost:9111/anyfig/io.osowa.anyfig.examples.Example$Settings.minSpeed'

We can now confirm this operation by getting for this particular field:

    $ curl -H 'Authorization: s3kret' 'http://localhost:9111/anyfig/io.osowa.anyfig.examples.Example$Settings.minSpeed'
    {"value": 10.0}

Naturally, Anyfig [uses itself](https://github.com/kushmerick/anyfig/blob/master/src/main/java/io/osowa/anyfig/Anyfig.java#L475-L477) to configure
the Remote API.  See [RemoteAPI.Config](https://github.com/kushmerick/anyfig/blob/master/src/main/java/io/osowa/anyfig/api/RemoteAPI.java#L17-L45)
for some advanced Anyfig features, such as blocking fields from being configurable by the Remote API, and redacting fields when using the
automagic Logger callbacks.

# Usage

* TODO: Coming soon... consume as a Maven dependency...

# Comparison

Anyfig is more ambitious than [Apache Commons Configuration](http://commons.apache.org/proper/commons-configuration).
ACC allows you to write code to pull configuration from various sources.  But Anyfig goes further: it writes these
values into the objects that hold your configuration, invokes callbacks so that your code can respond appropriately,
supports a Remote API.  On the other hand, Anyfig does not aspire to be a full-featured dependency-injection framework
like [Guice](https://github.com/google/guice) or [Spring](https://spring.io).

# Coming Soon

* TODO: Cluster support: Anyfig will support propogating Remote API changes across the members of a cluster.

* TODO: History & Rollback
