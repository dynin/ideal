# Copyright 2014 The Ideal Authors. All rights reserved.
#
# Use of this source code is governed by a BSD-style
# license that can be found in the LICENSE file or at
# https://developers.google.com/open-source/licenses/bsd

THIRD_PARTY_DIR = thirdparty

JDK_DIR = $(THIRD_PARTY_DIR)/jdk
JDK_VERSION = 1.6

BUILD_DIR = build
CLASSES_DIR = $(BUILD_DIR)/classes
TARGETS_DIR = $(BUILD_DIR)/targets
GENERATED_DIR = $(BUILD_DIR)/generated
JAVADOC_DIR = $(BUILD_DIR)/javadoc
PRETTY_DIR = $(BUILD_DIR)/pretty
SCRATCH_DIR = $(BUILD_DIR)/scratch

MKDIR = mkdir -p

JSR305_JAR = $(THIRD_PARTY_DIR)/jsr305.jar
JUNIT_JAR = $(THIRD_PARTY_DIR)/junit-4.11.jar
JAVACUP_JAR = $(THIRD_PARTY_DIR)/java-cup-11b.jar
GSON_JAR = $(THIRD_PARTY_DIR)/gson-2.2.4.jar
APPENGINE_VERSION = 1.9.6
APPENGINE_DIR = $(THIRD_PARTY_DIR)/appengine-java-sdk-$(APPENGINE_VERSION)
APPENGINE_SDK_JAR = $(APPENGINE_DIR)/lib/user/appengine-api-1.0-sdk-$(APPENGINE_VERSION).jar
SERVLET_JAR = $(APPENGINE_DIR)/lib/shared/servlet-api.jar

THIRD_PARTY_JARS = $(JSR305_JAR):$(JUNIT_JAR):$(JAVACUP_JAR):$(GSON_JAR)
APPENGINE_JARS = $(APPENGINE_SDK_JAR):$(SERVLET_JAR)

BOOTSTRAPPED_DIR = bootstrapped

JAVA = $(JDK_DIR)/bin/java -ea -classpath $(CLASSES_DIR):$(THIRD_PARTY_JARS)
JAVAC_SOURCE_OPTS = -source $(JDK_VERSION) -target $(JDK_VERSION)
JAVAC_OPTS = $(JAVAC_SOURCE_OPTS) \
        -classpath $(CLASSES_DIR):$(THIRD_PARTY_JARS) -d $(CLASSES_DIR) \
	-sourcepath .:$(GENERATED_DIR):$(BOOTSTRAPPED_DIR)
JAVAC = $(JDK_DIR)/bin/javac $(JAVAC_OPTS)
JAVAC_LINT_OPT = -Xlint:unchecked
JAVAC_OPTS_APPENGINE = $(JAVAC_SOURCE_OPTS) \
        -classpath $(THIRD_PARTY_JARS):$(APPENGINE_JARS):$(CLASSES_DIR) \
        -d $(CLASSES_DIR)
JAVAC_APPENGINE = $(JDK_DIR)/bin/javac $(JAVAC_OPTS_APPENGINE)
JAVADOC = $(JDK_DIR)/bin/javadoc -d $(JAVADOC_DIR)

PARSER_DIR = $(GENERATED_DIR)/generated/ideal/development/parsers
PARSER2SRC_DIR = ../../../../../..
JAVACUP = $(PARSER2SRC_DIR)/$(JDK_DIR)/bin/java \
        -classpath $(PARSER2SRC_DIR)/$(JAVACUP_JAR) java_cup.Main

BOOTSTRAPPED_TARGET = $(TARGETS_DIR)/boostrapped
LIBRARY_TARGET = $(TARGETS_DIR)/library
BASEPARSER_TARGET = $(TARGETS_DIR)/baseparser
IDEAL_TARGET = $(TARGETS_DIR)/ideal

CREATE_MAIN = ideal.development.tools.create
CREATE = $(JAVA) $(CREATE_MAIN)

FLAGS_PRETTY = -pretty-print -output=$(PRETTY_DIR)
FLAGS_RUN = -debug-passes -run
FLAGS_REFLECT = -debug-reflect

ISOURCE_DIR = isource
IDEAL_SOURCE = $(ISOURCE_DIR)/ideal.i
LIBRARY_ELEMENTS = $(ISOURCE_DIR)/library/elements.i
ONETWO = $(ISOURCE_DIR)/tests/12.i
ONE = $(ISOURCE_DIR)/tests/1.i
CIRCLE = $(ISOURCE_DIR)/showcase/circle.i
HELLO = $(ISOURCE_DIR)/showcase/hello.i
IDEAL_RUNTIME = $(ISOURCE_DIR)/idealruntime.i
# TODO: deprecate ISOURCES
ISOURCES = $(ISOURCE_DIR)/*

# Defintions for the Coach app
COACH_TARGET = $(TARGETS_DIR)/coach
COACH_WAR_TARGET = $(TARGETS_DIR)/coach-war

COACH_RESOURCES_DIR = ideal/showcase/coach/resources
COACH_IDEAL = $(COACH_RESOURCES_DIR)/coach.i
COACH_WAR_DIR = $(BUILD_DIR)/coach-war
COACH_WEB_INF_DIR = $(COACH_WAR_DIR)/WEB-INF
COACH_WAR_TEMPLATE = $(COACH_RESOURCES_DIR)/war-template
COACH_WAR_FILES = $(COACH_WAR_TEMPLATE)/* $(COACH_WAR_TEMPLATE)/WEB-INF/web.xml

# For profiling
AGENT_PATH = $(THIRD_PARTY_DIR)/yjp-12.0.6/bin/linux-x86-32/libyjpagent.so
JAVA_PROFILING_OPT = -agentpath:$(AGENT_PATH)=tracing
JAVA_PROFILING_OPT2 = -agentlib:hprof=cpu=times
CREATE_PROF = $(JAVA) $(JAVA_PROFILING_OPT) $(CREATE_MAIN)

BOOTSTRAPPED_JAVA = \
    $(BOOTSTRAPPED_DIR)/ideal/library/elements/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/library/channels/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/library/texts/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/library/resources/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/elements/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/texts/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/reflections/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/channels/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/resources/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/logs/*.java

MACHINE_JAVA = \
    ideal/machine/elements/*.java \
    ideal/machine/channels/*.java \
    ideal/machine/resources/*.java

PARSER_CUP = ideal/development/parsers/base_parser.cup

DEVELOPMENT_JAVA = \
    ideal/development/elements/*.java \
    ideal/development/texts/*.java \
    ideal/development/components/*.java \
    ideal/development/comments/*.java \
    ideal/development/annotations/*.java \
    ideal/development/names/*.java \
    ideal/development/flavors/*.java \
    ideal/development/modifiers/*.java \
    ideal/development/declarations/*.java \
    ideal/development/constructs/*.java \
    ideal/development/kinds/*.java \
    ideal/development/types/*.java \
    ideal/development/literals/*.java \
    \
    ideal/development/actions/*.java \
    ideal/development/notifications/*.java \
    ideal/development/values/*.java \
    ideal/development/functions/*.java \
    ideal/development/scanners/*.java \
    ideal/development/analyzers/*.java \
    ideal/development/templates/*.java \
    ideal/development/transformers/*.java \
    ideal/development/printers/*.java \
    ideal/development/extensions/*.java \
    ideal/development/parsers/*.java \
    ideal/development/documenters/*.java \
    ideal/development/tools/*.java \
    ideal/development/tests/*.java

SHOWCASE_COACH_JAVA = \
    ideal/showcase/coach/reflections/*.java \
    ideal/showcase/coach/marshallers/*.java \
    ideal/showcase/coach/forms/*.java \
    ideal/showcase/coach/common/*.java \
    ideal/showcase/coach/webforms/*.java \
    ideal/showcase/coach/appengine/*.java

default: ideal-run

all: $(IDEAL_TARGET)

ideal-run: $(IDEAL_TARGET) $(LIBRARY_ELEMENTS)
	$(CREATE) -debug-passes -input=$(IDEAL_SOURCE) -target=print_elements

library: $(IDEAL_TARGET) $(IDEAL_SOURCE)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_library

libraryb: $(IDEAL_TARGET) $(IDEAL_SOURCE)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_library -output=$(BOOTSTRAPPED_DIR)

libraryt: $(IDEAL_TARGET) $(IDEAL_SOURCE) rm-scratch
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_library -output=$(SCRATCH_DIR)
	$(JAVAC) $(SCRATCH_DIR)/ideal/library/*/*java

1: $(IDEAL_TARGET) $(ONE)
	$(CREATE) $(FLAGS_RUN) -input=$(ONE)

12: $(IDEAL_TARGET) $(ONETWO)
	$(CREATE) $(FLAGS_RUN) -input=$(ONETWO)

12p: $(IDEAL_TARGET) $(ONETWO)
	$(CREATE_PROF) $(FLAGS_RUN) -input=$(ONETWO)

circle: $(IDEAL_TARGET) $(CIRCLE)
	$(CREATE) $(FLAGS_RUN) -input=$(CIRCLE)

### Generating runtime

runtime: $(IDEAL_TARGET) $(IDEAL_RUNTIME)
	$(CREATE) -input=$(IDEAL_RUNTIME) -target=generate_runtime

runtimep: $(IDEAL_TARGET) $(IDEAL_RUNTIME)
	$(CREATE_PROF) -input=$(IDEAL_RUNTIME) -target=generate_runtime > /dev/null

runtimet: $(IDEAL_TARGET) $(IDEAL_RUNTIME) rm-scratch
	$(CREATE) -input=$(IDEAL_RUNTIME) -target=generate_runtime -output=$(SCRATCH_DIR)
	$(JAVAC) $(SCRATCH_DIR)/ideal/runtime/*/*java

runtimeb: $(IDEAL_TARGET) $(IDEAL_RUNTIME)
	$(CREATE) -input=$(IDEAL_RUNTIME) -target=generate_runtime -output=$(BOOTSTRAPPED_DIR)

runtimed: $(IDEAL_TARGET)
	$(CREATE) -debug-passes -input=$(IDEAL_RUNTIME) -target=document_runtime \
            -output=$(PRETTY_DIR)

### Generating other

reboot:
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_library -output=$(BOOTSTRAPPED_DIR)
	$(CREATE) -input=$(IDEAL_RUNTIME) -target=generate_runtime -output=$(BOOTSTRAPPED_DIR)

allt: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_library -output=$(SCRATCH_DIR)
	$(CREATE) -input=$(IDEAL_RUNTIME) -target=generate_runtime -output=$(BOOTSTRAPPED_DIR)
	$(JAVAC) $(SCRATCH_DIR)/ideal/*/*/*java

texts: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_RUNTIME) -target=generate_texts

textst: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -input=$(IDEAL_RUNTIME) -target=generate_texts -output=$(SCRATCH_DIR)
	$(JAVAC) $(SCRATCH_DIR)/ideal/runtime/texts/*java

textst_: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -input=$(IDEAL_RUNTIME) -target=generate_texts -output=$(SCRATCH_DIR)
	cp runtests.java  $(SCRATCH_DIR)/ideal/runtime/texts/
	$(JAVAC) $(SCRATCH_DIR)/ideal/runtime/texts/*java
	$(JAVA) -cp $(CLASSES_DIR) ideal.runtime.texts.runtests

textsb: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_RUNTIME) -target=generate_texts -output=$(BOOTSTRAPPED_DIR)

### Reflections runtime

reflect: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_RUNTIME) -target=generate_reflections

reflectt: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -input=$(IDEAL_RUNTIME) -target=generate_reflections -output=$(SCRATCH_DIR)
	$(JAVAC) $(SCRATCH_DIR)/ideal/runtime/reflections/*java

reflectb: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_RUNTIME) -target=generate_reflections -output=$(BOOTSTRAPPED_DIR)

### Other targets

import: $(IDEAL_TARGET)
	$(CREATE) -debug-import

pretty: $(IDEAL_TARGET)
	$(CREATE) -debug-passes -input=$(IDEAL_SOURCE) -target=document_elements \
            -output=$(PRETTY_DIR)

prettylib: $(IDEAL_TARGET)
	$(CREATE) -debug-passes -input=$(IDEAL_SOURCE) -target=document_library \
            -output=$(PRETTY_DIR)

prettylist: $(IDEAL_TARGET)
	$(CREATE) -debug-passes -input=$(IDEAL_RUNTIME) -target=document_elements \
            -output=$(PRETTY_DIR)

doc: $(IDEAL_TARGET)
	$(CREATE) -debug-passes -input=$(IDEAL_RUNTIME) -target=document_all \
            -output=$(PRETTY_DIR)

hello: $(IDEAL_TARGET)
	$(CREATE) $(FLAGS_RUN) -input=$(HELLO)

diff: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=print_elements | diff -B -E - $(LIBRARY_ELEMENTS)

test: build $(IDEAL_TARGET)
	$(JAVA) ideal.development.tests.main

$(BOOTSTRAPPED_TARGET): $(BOOTSTRAPPED_JAVA) build
	$(JAVAC) $(BOOTSTRAPPED_JAVA)
	@touch $@
	@echo === Bootstrapped done.

$(LIBRARY_TARGET): $(MACHINE_JAVA) $(BOOTSTRAPPED_TARGET)
	$(JAVAC) $(MACHINE_JAVA)
	@touch $@
	@echo === Library done.

$(BASEPARSER_TARGET): $(PARSER_CUP)
	$(MKDIR) $(PARSER_DIR)
	cd $(PARSER_DIR) ; \
	  $(JAVACUP) \
	      -package generated.ideal.development.parsers \
	      -parser base_parser \
	      -symbols base_symbols \
	      $(PARSER2SRC_DIR)/$(PARSER_CUP)
	@touch $@
	@echo === Parser done.

$(IDEAL_TARGET): build $(DEVELOPMENT_JAVA) $(LIBRARY_TARGET) $(BASEPARSER_TARGET) Makefile
	$(JAVAC) $(DEVELOPMENT_JAVA)
	@touch $@
	@echo === ideal done.

buildall: $(IDEAL_TARGET) $(COACH_TARGET)

jdoc: build
	$(MKDIR) $(BUILD_DIR)/javadoc
	$(JAVADOC) $(BOOTSTRAPPED_JAVA) $(MACHINE_JAVA) $(DEVELOPMENT_JAVA)

grammar:
	cd $(PARSER_DIR) ; \
	  $(JAVACUP) -dump_grammar \
	      -package generated.ideal.development.parsers \
	      -parser base_parser \
	      -symbols base_symbols \
	      $(PARSER2SRC_DIR)/$(PARSER_CUP)

build:
	$(MKDIR) $(BUILD_DIR)
	$(MKDIR) $(BUILD_DIR)/classes
	$(MKDIR) $(BUILD_DIR)/targets
	$(MKDIR) $(BUILD_DIR)/generated

clean:
	rm -f `find $(CLASSES_DIR) -name \*.class`
	rm -f $(TARGETS_DIR)/*

rm-scratch:
	rm -rf $(SCRATCH_DIR)

wipeout:
	rm -rf $(BUILD_DIR)

testall: wipeout buildall test
	bin/regression.sh
	bin/doc-regression.sh

# Targets related to Coach app

coach: $(IDEAL_TARGET) $(COACH_TARGET) $(COACH_IDEAL)
	$(CREATE) $(FLAGS_REFLECT) -input=$(COACH_IDEAL)

$(COACH_TARGET): $(SHOWCASE_COACH_JAVA)
	$(JAVAC_APPENGINE) $(SHOWCASE_COACH_JAVA)
	@touch $@
	@echo === Coach done.

$(COACH_WAR_TARGET): $(COACH_TARGET) $(ISOURCES) $(COACH_WAR_FILES)
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

runserver: $(COACH_WAR_TARGET)
	$(APPENGINE_DIR)/bin/dev_appserver.sh $(COACH_WAR_DIR)

update: $(COACH_WAR_TARGET)
	$(APPENGINE_DIR)/bin/appcfg.sh update $(COACH_WAR_DIR)

rollback: $(COACH_WAR_TARGET)
	$(APPENGINE_DIR)/bin/appcfg.sh rollback $(COACH_WAR_DIR)

MINI_DIR = ideal/experiment/mini
MINI_SOURCE = $(MINI_DIR)/*.java
MINI_CREATE = ideal.experiment.mini.create
JAVA_MINI_CREATE = $(JDK_DIR)/bin/java -cp $(CLASSES_DIR) -ea $(MINI_CREATE)
MINITARGET = $(TARGETS_DIR)/mini
MINI_BOOTSTRAPPED = $(MINI_DIR)/bootstrapped.java
MINI_BOOTSTRAPPED_TMP = $(MINI_BOOTSTRAPPED).tmp

$(MINITARGET): $(MINI_SOURCE)
	$(JDK_DIR)/bin/javac -classpath $(JSR305_JAR) -Xlint:unchecked -d $(CLASSES_DIR) $^
	@touch $@

mini: $(MINITARGET)
	@$(JAVA_MINI_CREATE) -analyze $(MINI_DIR)/test.i

minib: $(MINITARGET)
	@cat $(MINI_DIR)/header.txt
	@$(JAVA_MINI_CREATE) $(MINI_DIR)/bootstrapped.i | sed s'/^/  /'
	@echo }

miniboot: $(MINITARGET)
	@echo Bootstrapping $(MINI_BOOTSTRAPPED)
	@make -s minib > $(MINI_BOOTSTRAPPED_TMP)
	@mv $(MINI_BOOTSTRAPPED_TMP)  $(MINI_BOOTSTRAPPED)
