#!/bin/sh

FROM='j_adapter'
TO='java_adapter'

#cd $HOME/Projects/ideal

ALLFILES="isource/*.i \
    isource/*/*.i \
    isource/*/*/*.i \
    testdata/* \
    bootstrapped/ideal/*/*/*.java \
    ideal/*/*/*.java \
    ideal/*/*/*.cup \
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
