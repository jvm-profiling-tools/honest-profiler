honest-profiler
===============

An attempt to have a usable and honest profiler for the JVM.

Design
------

The program has two major components. There is a small C++ jvmti agent which
writes out a log file describing a profile of the application it has been
attached to. Then a Java application can render/display a profile based on this
log. Generating a log means that profile can be analysed retrospective/offline.
It should be possible to do online profiling by writing the log file into a
memory mapped file which can then be concurrently read by the Java application.

Honest profiler takes the same approach outlined by Jeremy Manson where calls
are made to the `AsyncGetCallTrace` jvm method which avoids the need for threads
to reach a safe point in order to read their call trace. Consequently it avoids
a number of profiler accuracy issues that other sampling profilers suffer from.

The downside of using this method is that the code in your async callback has
horrific restrictions on it. What honest profiler does is copy the current
stack trace into a non-blocking, allocation free, circular MPSC queue. These
stack traces are then read by another thread which writes out the log file and
looks up information about useful things like methods names.

Based upon code originally open sourced by Jeremy Manson/Google:
http://jeremymanson.blogspot.co.uk/2013/07/lightweight-asynchronous-sampling.html

Installing Dependencies
-----------------------

For Java dependencies look in the `pom.xml` file, these will be installed
automatically, apart from the protobuf compiler. Native dependencies are:

unittest++ - a unit testing library
boost iostreams - used for portable memory mapped files
protobuf-compiler - the compiler for protobuf messages

To install :

 * On debian/ubuntu:

        sudo apt-get install libunittest++-dev libboost-iostreams-dev protobuf-compiler npm

 * On Arch Linux:

        yaourt -S base-devel unittestpp boost boost-libs protobuf nodejs

Notes on JDK Version
--------------------

The profiling agent will happily run under OpenJDK, but in order to use the Javafx UI you
need to use the Oracle JDK.


Compiling
---------

```
export LC_ALL=C
mvn package
```

If you just want to compile the C++ code then the `Makefile` works independently
of maven. You must have `$JAVA_HOME`  pointing to a JDK install in order to
compile the C++ component because it relies on jvmti.h.

Running
-------

*This isn't production ready code!*

* To run the jvmti agent: `java -agentpath:/path/to/location/liblagent.so <normal-java-commandline>`
* To run the gui client: `java -cp $JAVA_HOME/lib/tools.jar:target/honest-profiler-1.0-SNAPSHOT-jar-with-dependencies.jar com.insightfullogic.honest_profiler.delivery.javafx.JavaFXEntry`

Further Developer Documentation
===============================

Javascript Dependencies
-----------------------

These are automatically installed by maven, but just in case you need to know:

We have two kinds of dependencies in this project: tools and angular framework code.  The tools help
us manage and test the application.

* We get the tools we depend upon via `npm`, the [node package manager][npm].
* We get the angular code via `bower`, a [client-side code package manager][bower].

Behind the scenes this will also call `bower install`.  You should find that you have two new
folders in your project.

* `node_modules` - contains the npm packages for the tools we need
* `app/bower_components` - contains the angular framework files

*Note that the `bower_components` folder would normally be installed in the root folder but
angular-seed changes this location through the `.bowerrc` file.  Putting it in the app folder makes
it easier to serve the files by a webserver.*


## Javascript Directory Layout

All of the HTML/Javascript code lies in src/{main,test}/webapp.

    app/                --> all of the files to be used in production
      css/              --> css files
      img/              --> image files
      index.html        --> app layout file (the main html template file of the app)
      js/               --> javascript files
      partials/             --> angular view partials (partial html templates)

    test/               --> test config and source files
      protractor-conf.js    --> config file for running e2e tests with Protractor
      e2e/                  --> end-to-end specs
      karma.conf.js         --> config file for running unit tests with Karma
      unit/                 --> unit level specs/tests

Javascript Unit Tests
---------------------

These are written in
[Jasmine][jasmine], which we run with the [Karma Test Runner][karma]. We provide a Karma
configuration file to run them.

* the configuration is found at `test/karma.conf.js`
* the unit tests are found in `test/unit/`.

You can run just the javascript unit tests with:

```
npm test
```

