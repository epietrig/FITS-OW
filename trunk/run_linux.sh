#!/bin/sh

export LD_PRELOAD=/usr/lib/x86_64-linux-gnu/libpython2.7.so.1.0

#java -Xmx1024M -Xms512M -Djava.library.path=/usr/local/lib/python2.7/dist-packages/jep -Dcom.sun.media.jai.disableMediaLib=true -jar target/fits-ow-0.1.jar "$@"
java -Xmx1024M -Xms512M -Djava.library.path=/home/aibsen/Virtualenvs/fits-ow/lib/python2.7/site-packages/jep -Dcom.sun.media.jai.disableMediaLib=true -jar target/fits-ow-0.1.jar "$@"
