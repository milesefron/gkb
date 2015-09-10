#!/bin/sh

if [ -z "$IRUTILS_HOME" ]
then
   IRUTILS_HOME=.
fi

java -Xmx4g -Djava.library.path=/usr/local/lib/ -cp "target/gkb-0.0.1-SNAPSHOT.jar:target/lib/*" $@
