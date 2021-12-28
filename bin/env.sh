#!/bin/sh

BINDIR=`dirname $0`
ROOTDIR=`dirname $BINDIR`

THIRDPARTY="${ROOTDIR}/thirdparty"
JDK_DIR="${THIRDPARTY}/jdk"
ANTLR_JAR="${THIRDPARTY}/antlr-4.9.2-complete.jar"
CLASSPATH="${ROOTDIR}/build/classes:${THIRDPARTY}/java-cup-11b-runtime.jar:${ANTLR_JAR}"
JAVA_OPTS="-ea -classpath ${CLASSPATH}"
JAVA="${JDK_DIR}/bin/java ${JAVA_OPTS}"
JAVAC="${JDK_DIR}/bin/javac -classpath ${CLASSPATH}"

MAINCLASS="ideal.development.tools.create"

BASEARGS="-hide-declarations"
RUNARGS="$BASEARGS -debug-progress -debug-constructs -print -run"
DOCARGS="$BASEARGS -pretty-print"
TEXTDOCARGS="$BASEARGS -print"

IRUN="${JAVA} ${MAINCLASS} ${RUNARGS}"
IDOC="${JAVA} ${MAINCLASS} ${DOCARGS}"
ITEXTDOC="${JAVA} ${MAINCLASS} ${TEXTDOCARGS}"

TESTDIR="${ROOTDIR}/testdata"
FILES=`cd ${TESTDIR}; echo *.i | sed s/\\\.i//g`

BUILDI="make build/targets/ideal"
DIFF="diff -c"
#DIFF="diff -c -w"
