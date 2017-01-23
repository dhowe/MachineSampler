#!/bin/sh

cd ../bin/

# use java 1.5

java -Djava.library.path=.. -classpath .:../core.jar:../jsyn.jar ms.MachineSampler
