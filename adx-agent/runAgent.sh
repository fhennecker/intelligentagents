#!/bin/bash
#
# Usage
#   sh ./runServer.sh
#

TACAA_HOME=`pwd`
echo $TACAA_HOME
echo $CLASSPATH

javac -cp lib/adx-1.3.0.jar TrialAdNetwork.java
jar cf TrialAdNetwork.jar TrialAdNetwork.class TrialAdNetwork\$CampaignData.class
mv TrialAdNetwork.jar lib/
rm TrialAdNetwork.class TrialAdNetwork\$CampaignData.class
java -cp "lib/*" tau.tac.adx.agentware.Main -config config/aw-1.conf
