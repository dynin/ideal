#!/bin/sh
. bin/env.sh

if $BUILDI
then
  echo "Build ok"
else
  echo "Build error"
  exit 1
fi

make buildall

for f in $FILES
do
  echo "=== ${f} ==="
  ${IDOC} -input=${TESTDIR}/${f}.i | ${DIFF} ${TESTDIR}/${f}-out.html -
  ${ITEXTDOC} -input=${TESTDIR}/${f}.i | ${DIFF} ${TESTDIR}/${f}-out.txt -
done
