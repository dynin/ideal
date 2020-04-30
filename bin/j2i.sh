#!/bin/sh

IDEAL=ideal.i
ALLFILES=jsource/ideal/development/elements/*.java 

for OLDFILE in $ALLFILES
do
  NEWFILE=tmp/`basename "${OLDFILE}" .java`.i
  echo ==$NEWFILE
  head -n 6 $IDEAL > $NEWFILE
  tail -n +11 < $OLDFILE >> $NEWFILE
done
