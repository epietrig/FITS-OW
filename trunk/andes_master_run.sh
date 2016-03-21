#!/bin/bash

JARS="target/aspectjrt-1.8.9.jar"
JARS=$JARS":target/jgroups-3.6.8.Final.jar"
JARS=$JARS":target/log4j-1.2.17.jar"
JARS=$JARS":target/slf4j-api-1.7.10.jar"
JARS=$JARS":target/slf4j-log4j12-1.7.10.jar"
JARS=$JARS":target/timingframework-1.0.jar"
JARS=$JARS":target/xercesImpl-2.8.1.jar"
JARS=$JARS":target/xml-apis-1.3.03.jar"
JARS=$JARS":target/xmlParserAPIs-2.6.2.jar"
JARS=$JARS":target/zvtm-svg-0.2.1.jar"
JARS=$JARS":target/commons-logging-1.1.jar"
JARS=$JARS":target/args4j-2.0.29.jar"
JARS=$JARS":target/fits-ow-0.1.jar"

rm target/zvtm-core-0.12.0-SNAPSHOT.jar

IP=192.168.1.213

#python src/main/resources/scripts/wcs/daemon_wcsCoordinates.py &

java -Djava.net.preferIPv4Stack=true -Djgroups.bind_addr="$IP" -Xmx8192M -Xms2048M -Dcom.sun.media.jai.disableMediaLib=true -cp .:$JARS fr.inria.ilda.fitsow.WallFITSOW -r 4 -c 6 -bw 2020 -bh 1180 "$@"

#echo "killall -9 python"
#killall -9 python
#!/bin/bash

JARS="target/aspectjrt-1.8.9.jar"
JARS=$JARS":target/jgroups-3.6.8.Final.jar"
JARS=$JARS":target/log4j-1.2.17.jar"
JARS=$JARS":target/slf4j-api-1.7.10.jar"
JARS=$JARS":target/slf4j-log4j12-1.7.10.jar"
JARS=$JARS":target/timingframework-1.0.jar"
JARS=$JARS":target/xercesImpl-2.8.1.jar"
JARS=$JARS":target/xml-apis-1.3.03.jar"
JARS=$JARS":target/xmlParserAPIs-2.6.2.jar"
JARS=$JARS":target/commons-logging-1.1.jar"
JARS=$JARS":target/args4j-2.0.29.jar"
JARS=$JARS":target/zvtm-fits-0.2.0-SNAPSHOT.jar"
JARS=$JARS":target/zvtm-svg-0.2.2-SNAPSHOT.jar"

JARS=$JARS":target/fits-ow-0.1.jar"

rm target/zvtm-core-0.12.0-SNAPSHOT.jar

IP=192.168.1.213

#python src/main/resources/scripts/wcs/daemon_wcsCoordinates.py &

java -Djava.net.preferIPv4Stack=true -Djgroups.bind_addr="$IP" -Xmx4096M -Xms2048M -Dcom.sun.media.jai.disableMediaLib=true -cp .:$JARS fr.inria.ilda.fitsow.WallFITSOW -r 4 -c 6 -bw 2020 -bh 1180 "$@"

#echo "killall -9 python"
#killall -9 python
