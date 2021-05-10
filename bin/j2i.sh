#!/bin/sh

PACKAGE=types
TMPDIR=tmp/i
IDEAL=ideal.i
ALLFILES="jsource/ideal/development/${PACKAGE}/*.java"
PKGFILE=${TMPDIR}/${PACKAGE}.i

mkdir -p $TMPDIR

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
