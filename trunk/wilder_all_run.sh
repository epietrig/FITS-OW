#!/bin/bash

# usage example:
#		./wilder-all-run.sh -ip=144 -n=Sample -c=ilda.zcsample.Sample -l=chapuis -- other option
# will run slaves for each cluster screen, joining the application named 'Sample'
# and then run the master application

#echo "$@"


LOGIN="wild"
NAME="WallFITSOW"
CLASS="fr.inria.ilda.fitsow.WallFITSOW"
LIP="2"
accum_rest=0
REST=""

#  /home2/wild/workspace/zvtm/zuist-cluster/trunk
#ZVTMHOME="/media/data/Demos/zvtm/lib"
#ZVTMHOME="/home2/wild/workspace/zvtm/zuist-cluster/trunk"
#ZVTMHOME="/home2/wild/workspace/zc-sample"
ZVTMHOME="/home2/wild/workspace/zvtm_demos/FITS-OW/trunk"

export LD_LIBRARY_PATH="/usr/lib/jvm/default-java/jre/lib/amd64/"

for i in "$@"
do
case $i in
    -n=*)
      if [ $accum_rest == 1 ];
        then REST="$REST $i";
      else
    	 NAME="${i#*=}"
      fi
    ;;
    -c=*)
      if [ $accum_rest == 1 ];
        then REST="$REST $i";
      else
    	   CLASS="${i#*=}"
      fi
    ;;
    -ip=*)
      if [ $accum_rest == 1 ];
        then REST="$REST $i";
      else
    	   #LIP="192.168.0.${i#*=}"
        LIP="${i#*=}"
      fi
    ;;
    -l=*)
      if [ $accum_rest == 1 ];
        then REST="$REST $i";
      else
        LOGIN="${i#*=}"
      fi
    ;;
    --)
  		accum_rest=1
    ;;
    *)
		if [ $accum_rest == 1 ];
			then REST="$REST $i";
		fi
    ;;
esac
#shift
done

echo "LOGIN: " $LOGIN
echo "NAME: " $NAME
echo "CLASS: " $CLASS
echo "REST: " $REST
echo "LIP: " $LIP

#exit

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
  esac
}

function colIP {
  case "$1" in
          "a" ) return 0;;
          "b" ) return 1;;
  esac
}

function startId {
  case "$1" in
      "a" ) return 0;;
      "b" ) return 40;;
  esac
}


function blockNum {
  case "$1" in
      "a" ) return 8;;
      "b" ) return 7;;
  esac
}

for col in {a..b}
do
    for row in {1..5}
    do
        SLAVENUM=`expr $row - 1`
        startId $col
        SLAVENUM=`expr $? + $SLAVENUM`
        colIP $col
        startIp=`expr $? + 1`
        startIp=`expr $startIp \* 10`
        startIp=`expr $startIp + $row`
        blockNum $col
        BLOCKNB=$?
        echo "$LOGIN@$col$row"
        echo "-Djgroups.bind_addr=\"192.168.2.$startIp\" Slavenum: $SLAVENUM $BLOCKNB"
        ssh $LOGIN@192.168.2.$startIp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "export DISPLAY=localhost:0.0 ; cd $ZVTMHOME ; java -server -Djava.net.preferIPv4Stack=true -Djgroups.bind_addr=\"192.168.2.$startIp\" -cp $JARS fr.inria.zvtm.cluster.SlaveApp -b $SLAVENUM -wb $BLOCKNB -u -f -g -a -n $NAME" &
        sleep 1
      done
done


sleep 3
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


#java -Djava.net.preferIPv4Stack=true -Djgroups.bind_addr="192.168.0.56" -Djava.library.path=".:lib" -cp "target/*" $CLASS $REST
java -Djava.net.preferIPv4Stack=true -Djgroups.bind_addr="192.168.2.$LIP" -Djava.library.path=".:lib:/usr/lib/jvm/java-1.7.0-openjdk-amd64/jre/lib/amd64" -cp $JARS $CLASS -r 5 -c 15 -bw 960 -bh 960 $REST

WALL=WILDER  walldo -l $LOGIN killall java
#WALL=WILDER walldo -l wild killall java
#walldo  killall java
