#!/bin/sh

FROM='supertype_kind'
TO='subtype_tag'

#cd $HOME/Projects/ideal

ALLFILES="*.i \
    */*.i \
    */*/*.i \
    testdata/* \
    bootstrapped/ideal/*/*/*.java \
    jsource/ideal/*/*/*.java \
    jsource/ideal/*/*/*.cup \
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
