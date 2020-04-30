#!/bin/sh
. bin/env.sh

if [ x"$1" != x ]
then
  SOURCE="$1"
else
  SOURCE="testdata/bootstrap.i"
fi

$IDOC -input=${SOURCE}
