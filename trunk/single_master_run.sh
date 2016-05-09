#!/bin/bash

#IP="192.168.6.90"
IP="192.168.1.10"
#IP="129.175.5.15"


JARS="target/aspectjrt-1.8.9.jar"
JARS=$JARS":target/jgroups-3.6.8.Final.jar"
JARS=$JARS":target/log4j-1.2.17.jar"
JARS=$JARS":target/slf4j-api-1.7.10.jar"
JARS=$JARS":target/slf4j-log4j12-1.7.10.jar"
JARS=$JARS":target/timingframework-1.0.jar"
JARS=$JARS":target/xercesImpl-2.8.1.jar"
JARS=$JARS":target/xml-apis-1.3.03.jar"
JARS=$JARS":target/xmlParserAPIs-2.6.2.jar"
JARS=$JARS":target/zvtm-svg-0.2.2-SNAPSHOT.jar"
JARS=$JARS":target/commons-logging-1.1.jar"
JARS=$JARS":target/args4j-2.0.29.jar"
JARS=$JARS":target/fits-ow-0.1.jar"

java -server -Djava.net.preferIPv4Stack=true -Djava.library.path=/Library/Python/2.7/site-packages/jep -Djgroups.bind_addr=$IP -Dcom.sun.media.jai.disableMediaLib=true -Xmx1g -cp .:$JARS fr.inria.ilda.fitsow.WallFITSOW -r 1 -c 1 -bw 800 -bh 600 "$@"
