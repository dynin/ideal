#!/bin/sh

THIRDPARTY=thirdparty
INSTALL_APPENGINE=false

if [ ! -f ideal.i -o ! -d jsource ] ; then
  echo This script must be run from the top-level directory.
  exit
else
  echo Preparing to initialize ${THIRDPARTY}.
fi

if [ ! -d ${THIRDPARTY} ] ; then
  mkdir ${THIRDPARTY}
fi

cd ${THIRDPARTY}

MAVEN2=https://repo1.maven.org/maven2

echo Fetching JSR 305 annotations via http://findbugs.sourceforge.net/
JSR305_VERSION=3.0.2
curl -O ${MAVEN2}/com/google/code/findbugs/jsr305/${JSR305_VERSION}/jsr305-${JSR305_VERSION}.jar
echo

echo Fetching ANTLR via https://www.antlr.org/
ANTLR_VERSION=4.9.2
ANTLR_JAR=antlr-${ANTLR_VERSION}-complete.jar
curl -O https://www.antlr.org/download/${ANTLR_JAR}
echo

echo Fetching JavaCUP via http://www2.cs.tum.edu/projects/cup/
JAVACUP_VERSION=11b-20151001
JAVACUP_TGZ=java-cup-bin-${JAVACUP_VERSION}.tar.gz
curl -O http://www2.cs.tum.edu/projects/cup/releases/${JAVACUP_TGZ}
tar xfz ${JAVACUP_TGZ}
echo

if [ $INSTALL_APPENGINE = true ] ; then
  echo Fetching Google Gson via https://github.com/google/gson
  GSON_VERSION=2.8.6
  curl -O ${MAVEN2}/com/google/code/gson/gson/${GSON_VERSION}/gson-${GSON_VERSION}.jar
  echo

  echo Fetching AppEngine SDK for Java via \
      https://cloud.google.com/appengine/docs/standard/java/download
  APPENGINE_VERSION=1.9.78
  APPENGINE_ZIP=appengine-java-sdk-${APPENGINE_VERSION}.zip
  curl -O https://storage.googleapis.com/appengine-sdks/featured/${APPENGINE_ZIP}
  unzip -q ${APPENGINE_ZIP}
  echo
else
  echo Skipping AppEngine SDK.
fi

JDK=jdk
if [ ! -d ${JDK} ] ; then
  echo ${THIRDPARTY}/${JDK} must be symlinked to JDK home.
  echo Trying to detect JDK home automatically--should work on macOS...
  ln -s "`/usr/libexec/java_home`" ${JDK}
fi
