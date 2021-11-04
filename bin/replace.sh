#!/bin/sh

FROM='procedure1_composite_data_value'
TO='procedure1_data_value'

#cd $HOME/Projects/ideal

ALLFILES="`find experimental -name \*.java`"

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
