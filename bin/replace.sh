#!/bin/sh

FROM='2020'
TO='2021'

#cd $HOME/Projects/ideal

ALLFILES="`find library runtime machine development experimental showcase -name \*.i` \
    `find jsource bootstrapped -name \*.java` \
    jsource/ideal/*/*/*.cup \
    testdata/* \
    Makefile"

for OLDFILE in $ALLFILES
do
  NEWFILE=`echo "${OLDFILE}" | sed "s/${FROM}/${TO}/g"`
  echo "${OLDFILE} -> ${NEWFILE}"
  TMPFILE="${NEWFILE}.tmp"
  sed "s/${FROM}/${TO}/g" < $OLDFILE > $TMPFILE
  diff $OLDFILE $TMPFILE
  if [ $OLDFILE != $NEWFILE ]
  then
    git mv $OLDFILE $NEWFILE
    rm $OLDFILE
  fi
  mv -f $TMPFILE $NEWFILE
done
