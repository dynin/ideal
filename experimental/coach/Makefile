# Copyright 2014-2022 The Ideal Authors. All rights reserved.
#
# Use of this source code is governed by a BSD-style
# license that can be found in the LICENSE file or at
# https://theideal.org/license/

ROOT_DIR = ../..
THIRD_PARTY_DIR = $(ROOT_DIR)/thirdparty

JDK_DIR = $(THIRD_PARTY_DIR)/jdk
JDK_VERSION = 1.8

BUILD_DIR = $(ROOT_DIR)/build
CLASSES_DIR = $(BUILD_DIR)/classes
TARGETS_DIR = $(BUILD_DIR)/targets

MKDIR = mkdir -p

JSR305_JAR = $(THIRD_PARTY_DIR)/jsr305-3.0.2.jar
JUNIT_JAR = $(THIRD_PARTY_DIR)/junit-4.13.jar
JAVACUP_JAR = $(THIRD_PARTY_DIR)/java-cup-11b.jar
GSON_JAR = $(THIRD_PARTY_DIR)/gson-2.8.6.jar
APPENGINE_VERSION = 1.9.78
APPENGINE_DIR = $(THIRD_PARTY_DIR)/appengine-java-sdk-$(APPENGINE_VERSION)
APPENGINE_SDK_JAR = $(APPENGINE_DIR)/lib/user/appengine-api-1.0-sdk-$(APPENGINE_VERSION).jar
SERVLET_JAR = $(APPENGINE_DIR)/jetty94/jetty-home/lib/servlet-api-3.1.jar

THIRD_PARTY_JARS = $(JSR305_JAR):$(JUNIT_JAR):$(JAVACUP_JAR)
APPENGINE_JARS = $(GSON_JAR):$(APPENGINE_SDK_JAR):$(SERVLET_JAR)

JAVA = $(JDK_DIR)/bin/java -ea -classpath $(CLASSES_DIR):$(THIRD_PARTY_JARS)
#JAVAC_SOURCE_OPTS = -source $(JDK_VERSION) -target $(JDK_VERSION) -Xlint:deprecation
JAVAC_SOURCE_OPTS = -Xlint:deprecation
JAVAC_OPTS_APPENGINE = $(JAVAC_SOURCE_OPTS) \
        -classpath $(THIRD_PARTY_JARS):$(APPENGINE_JARS):$(CLASSES_DIR) \
        -d $(CLASSES_DIR)
JAVAC_APPENGINE = $(JDK_DIR)/bin/javac $(JAVAC_OPTS_APPENGINE)

IDEAL_TARGET = $(TARGETS_DIR)/ideal
COACH_TARGET = $(TARGETS_DIR)/coach
COACH_WAR_TARGET = $(TARGETS_DIR)/coach-war

CREATE_MAIN = ideal.development.tools.create
CREATE = $(JAVA) $(CREATE_MAIN)

ISOURCE_DIR = ../..

COACH_RESOURCES_DIR = ideal/showcase/coach/resources
COACH_IDEAL = $(COACH_RESOURCES_DIR)/coach.i
COACH_WAR_DIR = $(BUILD_DIR)/coach-war
COACH_WEB_INF_DIR = $(COACH_WAR_DIR)/WEB-INF
COACH_WAR_TEMPLATE = $(COACH_RESOURCES_DIR)/war-template
COACH_WAR_FILES = $(COACH_WAR_TEMPLATE)/* $(COACH_WAR_TEMPLATE)/WEB-INF/web.xml

SHOWCASE_COACH_JAVA = \
    ideal/showcase/coach/reflections/*.java \
    ideal/showcase/coach/marshallers/*.java \
    ideal/showcase/coach/forms/*.java \
    ideal/showcase/coach/common/*.java \
    ideal/showcase/coach/webforms/*.java \
    ideal/showcase/coach/appengine/*.java

$(COACH_TARGET): $(SHOWCASE_COACH_JAVA) $(IDEAL_TARGET)
	$(JAVAC_APPENGINE) $(SHOWCASE_COACH_JAVA)
	@touch $@
	@echo === Coach done.

coach: $(IDEAL_TARGET) $(COACH_TARGET) $(COACH_IDEAL)
	$(CREATE) -top=$(ISOURCE_DIR) -debug-reflect -input=$(COACH_IDEAL)

$(COACH_WAR_TARGET): $(COACH_TARGET) $(COACH_WAR_FILES)
	$(MKDIR) $(COACH_WEB_INF_DIR)
	$(MKDIR) $(COACH_WEB_INF_DIR)/lib
	$(MKDIR) $(COACH_WEB_INF_DIR)/classes
	$(MKDIR) $(COACH_WEB_INF_DIR)/isource
	cp -r $(CLASSES_DIR)/* $(COACH_WEB_INF_DIR)/classes
	cp -r $(COACH_WAR_TEMPLATE)/* $(COACH_WAR_DIR)
	cp -r $(ISOURCE_DIR)/library $(COACH_WEB_INF_DIR)/isource
	cp $(COACH_IDEAL) $(COACH_RESOURCES_DIR)/runtime.js $(COACH_WEB_INF_DIR)/isource
	cp `find  $(APPENGINE_DIR)/lib/user/ -name \*jar` $(JAVACUP_JAR) $(COACH_WEB_INF_DIR)/lib/
	cp $(GSON_JAR) $(COACH_WEB_INF_DIR)/lib/
	@touch $@

buildcoach: $(IDEAL_TARGET)
	$(JAVAC_APPENGINE) $(SHOWCASE_COACH_JAVA)
	@echo === Coach done.

runserver: $(COACH_WAR_TARGET)
	$(APPENGINE_DIR)/bin/dev_appserver.sh $(COACH_WAR_DIR)

update: $(COACH_WAR_TARGET)
	$(APPENGINE_DIR)/bin/appcfg.sh update $(COACH_WAR_DIR)

rollback: $(COACH_WAR_TARGET)
	$(APPENGINE_DIR)/bin/appcfg.sh rollback $(COACH_WAR_DIR)
