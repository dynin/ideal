#!/bin/sh

cd /home/dynin/ideal

LOG=../briefing/run.log

echo ============= >> $LOG
date >> $LOG
thirdparty/jdk/bin/java -ea -Dfile.encoding=UTF-8 -Djava.net.preferIPv6Addresses=true \
  -classpath build/classes:thirdparty/jsr305-3.0.2.jar:thirdparty/java-cup-11b.jar \
  ideal.showcase.briefing 2>&1 >> $LOG
