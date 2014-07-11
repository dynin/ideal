#!/bin/sh

#cd $HOME/Projects/ideal

ALLFILES="isource/*.i \
    isource/*/*.i \
    isource/*/*/*.i \
    testdata/* \
    bootstrapped/ideal/*/*/*.java \
    ideal/*/*/*.java \
    ideal/*/*/*.cup \
    Makefile"

for FILE in $ALLFILES
do
  echo "processing ${FILE}"
  TMPFILE="${FILE}.tmp"
  sed "s/[ ]*$//g" < $FILE > $TMPFILE
  diff $FILE $TMPFILE
  mv -f $TMPFILE $FILE
done
