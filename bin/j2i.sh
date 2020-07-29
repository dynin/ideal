#!/bin/sh

PACKAGE=origins
TMPDIR=tmp
IDEAL=ideal.i
ALLFILES="jsource/ideal/development/${PACKAGE}/*.java"
PKGFILE=${TMPDIR}/${PACKAGE}.i

head -n 6 $IDEAL > $PKGFILE

for OLDFILE in $ALLFILES
do
  NAME=`basename "${OLDFILE}" .java`
  echo ==$NAME
  echo "  interface ${NAME};" >> $PKGFILE
  NEWFILE=${TMPDIR}/${NAME}.i
  head -n 6 $IDEAL > $NEWFILE
  tail -n +11 < $OLDFILE >> $NEWFILE
done
