#!/bin/sh
. bin/env.sh

if [ x"$1" != x ]
then
  SOURCE="$1"
else
  SOURCE="isource/bootstrap.i"
fi

$IDOC -input=${SOURCE}
