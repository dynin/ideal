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
  ${IRUN} -input=${TESTDIR}/${f}.i | ${DIFF} ${TESTDIR}/${f}.out -
done
