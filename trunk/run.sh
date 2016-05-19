#!/bin/sh
java -Xmx1024M -Xms512M -Djava.library.path=/home/aibsen/Virtualenvs/fits-ow/local/lib/python2.7/site-packages/jep -Dcom.sun.media.jai.disableMediaLib=true -jar target/fits-ow-0.1.jar "$@"
