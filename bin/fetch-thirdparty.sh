#!/bin/sh

THIRDPARTY=thirdparty

if [ ! -d isource -o ! -d ideal ] ; then
  echo This script must be run from the top-level directory.
  exit
else
  echo Preparing to initialize ${THIRDPARTY}.
fi

if [ ! -d ${THIRDPARTY} ] ; then
  mkdir ${THIRDPARTY}
fi

cd ${THIRDPARTY}

echo Fetching JSR 305 annotations via https://code.google.com/p/jsr-305/
curl -O https://google-collections.googlecode.com/svn/trunk/lib/jsr305.jar
echo

echo Fetching JUnit4 via http://junit.org/
curl -O http://search.maven.org/remotecontent?filepath=junit/junit/4.11/junit-4.11.jar
echo

echo Fetching Google Gson via https://code.google.com/p/google-gson/
GSON_VERSION=2.2.4
GSON_ZIP=google-gson-${GSON_VERSION}-release.zip
GSON_DIR=google-gson-${GSON_VERSION}
GSON_JAR=gson-${GSON_VERSION}.jar
curl -O https://google-gson.googlecode.com/files/${GSON_ZIP}
unzip -q ${GSON_ZIP}
mv ${GSON_DIR}/${GSON_JAR} .
rm -rf ${GSON_DIR}
echo

echo Fetching JavaCUP via http://www2.cs.tum.edu/projects/cup/
JAVACUP_TGZ=java-cup-bin-11b-20140611.tar.gz
curl -O http://www2.cs.tum.edu/projects/cup/releases/${JAVACUP_TGZ}
tar xfz ${JAVACUP_TGZ}
echo

echo Fetching AppEngine SDK for Java via https://developers.google.com/appengine/downloads
APPENGINE_ZIP=appengine-java-sdk-1.9.6.zip
curl -O https://storage.googleapis.com/appengine-sdks/featured/${APPENGINE_ZIP}
unzip -q ${APPENGINE_ZIP}
echo

if [ ! -d jdk ] ; then
  echo Make sure ${THIRDPARTY}/jdk is symlinked to JDK home.
fi
