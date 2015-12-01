#!/bin/bash
#
# Usage
#   sh ./runServer.sh
#

TACAA_HOME=`pwd`
echo $TACAA_HOME
echo $CLASSPATH

javac -cp lib/adx-1.3.0.jar helpers/*.java
javac -cp lib/adx-1.3.0.jar:.:helpers AdNetwork.java
jar cf AdNetwork.jar AdNetwork.class helpers/*.class
mv AdNetwork.jar lib/
rm AdNetwork.class
java -cp "lib/*:helpers" tau.tac.adx.agentware.Main -config config/aw-1.conf
