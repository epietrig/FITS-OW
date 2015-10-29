#!/bin/sh
java -Xmx1024M -Xms512M -Dcom.sun.media.jai.disableMediaLib=true -jar target/fits-ow-0.1.jar "$@"
