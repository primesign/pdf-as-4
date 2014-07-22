#!/bin/sh
#
DEFAULT=`pwd`/catalina.env
. $DEFAULT

chmod +x $CATALINA_HOME/bin/catalina.sh

$CATALINA_HOME/bin/catalina.sh run

