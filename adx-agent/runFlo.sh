#!/bin/bash
#
# Usage
#   sh ./runServer.sh
#

TACAA_HOME=`pwd`
echo $TACAA_HOME
echo $CLASSPATH

javac -cp lib/adx-1.2.8.jar Florentin.java
jar cf Florentin.jar Florentin.class Florentin\$CampaignData.class
mv Florentin.jar lib/
rm Florentin.class Florentin\$CampaignData.class

java -cp "lib/*" tau.tac.adx.agentware.Main -config config/flo.conf
