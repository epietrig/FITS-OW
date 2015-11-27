#!/bin/bash

JARS="target/commons-logging-1.1.jar"
JARS=$JARS":target/args4j-2.0.29.jar"
JARS=$JARS":target/aspectjrt-1.8.6.jar"
JARS=$JARS":target/jgroups-2.7.0.GA.jar"
JARS=$JARS":target/log4j-1.2.17.jar"
JARS=$JARS":target/slf4j-api-1.7.10.jar"
JARS=$JARS":target/slf4j-log4j12-1.7.10.jar"
JARS=$JARS":target/timingframework-1.0.jar"
JARS=$JARS":target/fits-ow-0.1.jar"

function colNum {
  case "$1" in
          "a" ) return 0;;
          "b" ) return 2;;
          "c" ) return 4;;
  esac
}

#start client nodes
for col in {a..c}
do
  for row in {1..4}
    do
      colNum $col
      SLAVENUM1=`expr $? \* 4 + $row - 1`
      SLAVENUM2=`expr $SLAVENUM1 + 4`
      ssh wall@$col$row.wall.inria.cl -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "export DISPLAY=:0.0 && cd /home/wall/Andes/FITS-OW && java -XX:+DoEscapeAnalysis -XX:+UseConcMarkSweepGC -Djava.net.preferIPv4Stack=true -Djgroups.bind_addr="\"$col$row.wall.inria.cl\"" -Xmx8192M -Xms2048M -Dcom.sun.media.jai.disableMediaLib=true -cp .:$JARS fr.inria.zvtm.cluster.SlaveApp -n WallFITSOW -b $SLAVENUM1 -f" $* &
      sleep 1
      ssh wall@$col$row.wall.inria.cl -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "export DISPLAY=:0.1 && cd /home/wall/Andes/FITS-OW && java -XX:+DoEscapeAnalysis -XX:+UseConcMarkSweepGC -Djava.net.preferIPv4Stack=true -Djgroups.bind_addr="\"$col$row.wall.inria.cl\"" -Xmx8192M -Xms2048M -Dcom.sun.media.jai.disableMediaLib=true -cp .:$JARS fr.inria.zvtm.cluster.SlaveApp -n WallFITSOW -b $SLAVENUM2 -f" $* &
    done
done

[wall@master FITS-OW]$ cat andes_master_run.sh
#!/bin/bash

JARS="target/aspectjrt-1.8.6.jar"
JARS=$JARS":target/jgroups-2.7.0.GA.jar"
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

JARS="target/aspectjrt-1.8.6.jar"
JARS=$JARS":target/jgroups-2.7.0.GA.jar"
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
JARS=$JARS":target/zvtm-fits-0.2.0-SNAPSHOT.jar"
JARS=$JARS":target/zvtm-svg-0.2.2-SNAPSHOT.jar"

JARS=$JARS":target/fits-ow-0.1.jar"

rm target/zvtm-core-0.12.0-SNAPSHOT.jar

IP=192.168.1.213

#python src/main/resources/scripts/wcs/daemon_wcsCoordinates.py &

java -Djava.net.preferIPv4Stack=true -Djgroups.bind_addr="$IP" -Xmx4096M -Xms2048M -Dcom.sun.media.jai.disableMediaLib=true -cp .:$JARS fr.inria.ilda.fitsow.WallFITSOW -r 4 -c 6 -bw 2020 -bh 1180 "$@"

#echo "killall -9 python"
#killall -9 python
