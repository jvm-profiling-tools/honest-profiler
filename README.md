honest-profiler
===============
[![Build Status](https://travis-ci.org/jvm-profiling-tools/honest-profiler.svg?branch=master)](https://travis-ci.org/jvm-profiling-tools/honest-profiler)

A usable and honest profiler for the JVM. For documentation please refer to
[The Wiki](https://github.com/RichardWarburton/honest-profiler/wiki)

* [Download the Binary](http://insightfullogic.com/honest-profiler.zip)
* [How to Build](https://github.com/RichardWarburton/honest-profiler/wiki/How-to-build)
UNITTEST_INCLUDE_DIRS="/usr/local/Cellar/unittest-cpp/2.0.0/include/UnitTest++/" UNITTEST_LIBRARIES="UnitTest++" cmake CMakeLists.txt

[MAC]
# Use brew to install unittest++

# make sure unittest++ available to compliers : export paths
export LIBRARY_PATH=/usr/local/Cellar/unittest-cpp/2.0.0/lib/
export CPLUS_INCLUDE_PATH="/usr/local/Cellar/unittest-cpp/2.0.0/include/"
