#!/bin/sh

set -eu

rm -f log.hpl

java -agentpath:$PWD/build-32/liblagent.so -cp target/classes/ Example

xxd log.hpl | tail -n 30

