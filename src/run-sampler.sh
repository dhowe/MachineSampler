#!/bin/sh

cd ../bin/

# use java 1.5

/System/Library/Frameworks/JavaVM.framework/Versions/1.5/Commands/java -Djava.library.path=.. -classpath .:../core.jar:../jsyn.jar:../../OSCInterconnect/oscP5.jar:../../OSCInterconnect/bin ms.MachineSampler
