#!/bin/bash

JARS="target/commons-logging-1.1.jar"
JARS=$JARS":target/args4j-2.0.29.jar"
JARS=$JARS":target/aspectjrt-1.8.9.jar"
JARS=$JARS":target/jgroups-3.6.8.Final.jar"
JARS=$JARS":target/log4j-1.2.17.jar"
JARS=$JARS":target/slf4j-api-1.7.10.jar"
JARS=$JARS":target/slf4j-log4j12-1.7.10.jar"
JARS=$JARS":target/timingframework-1.0.jar"
JARS=$JARS":target/fits-ow-0.1.jar"


java -server -XX:+DoEscapeAnalysis -XX:+UseConcMarkSweepGC -Djava.net.preferIPv4Stack=true -Xmx4096M -Xms2048M -Djgroups.bind_addr=$IP -Dcom.sun.media.jai.disableMediaLib=true -cp .:$JARS fr.inria.zvtm.cluster.SlaveApp -n WallFITSOW -b 0
