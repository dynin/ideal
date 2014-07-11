#!/bin/sh

MAC_JDK_DIR="/System/Library/Frameworks/JavaVM.framework/Home"
OLD_JDK_DIR="/usr/local/java"
JDK_DIR="../software/jdk7"
CLASSPATH="build/classes:thirdparty/junit-4.11.jar:thirdparty/java-cup-11b-runtime.jar"
JAVA_OPTS="-ea -classpath ${CLASSPATH}"
JAVA="${JDK_DIR}/bin/java ${JAVA_OPTS}"

MAINCLASS="ideal.development.tools.create"

BASEARGS="-hide-declarations"
RUNARGS="$BASEARGS -debug-passes -debug-constructs -print -run"
DOCARGS="$BASEARGS -pretty-print"
TEXTDOCARGS="$BASEARGS -print"

IRUN="${JAVA} ${MAINCLASS} ${RUNARGS}"
IDOC="${JAVA} ${MAINCLASS} ${DOCARGS}"
ITEXTDOC="${JAVA} ${MAINCLASS} ${TEXTDOCARGS}"

TESTDIR="testdata"
FILES=`cd ${TESTDIR}; echo *.i | sed s/\.i//g`

BUILDI="make build/targets/ideal"
DIFF="diff -c"
#DIFF="diff -c -w"
