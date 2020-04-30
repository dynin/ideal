#!/bin/sh

#cd $HOME/Projects/ideal

ALLFILES="*.i \
    */*.i \
    */*/*.i \
    testdata/* \
    bootstrapped/ideal/*/*/*.java \
    jsource/ideal/*/*/*.java \
    jsource/ideal/*/*/*.cup \
    Makefile"

for FILE in $ALLFILES
do
  echo "processing ${FILE}"
  TMPFILE="${FILE}.tmp"
  sed "s/[ ]*$//g" < $FILE > $TMPFILE
  diff $FILE $TMPFILE
  mv -f $TMPFILE $FILE
done
