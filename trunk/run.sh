#!/bin/sh
java -Xmx1024M -Xms512M -Djava.library.path=/Library/Python/2.7/site-packages/jep -Dcom.sun.media.jai.disableMediaLib=true -jar target/fits-ow-0.1.jar "$@"
