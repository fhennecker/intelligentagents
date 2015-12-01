#!/bin/bash
#
# Usage
#   sh ./runServer.sh
#

TACAA_HOME=`pwd`
echo $TACAA_HOME
echo $CLASSPATH

javac -cp lib/adx-1.3.0.jar Agent.java
jar cf Agent.jar Agent.class Agent\$CampaignData.class
mv Agent.jar lib/
rm Agent.class Agent\$CampaignData.class
java -cp "lib/*" tau.tac.adx.agentware.Main -config config/aw-1.conf
