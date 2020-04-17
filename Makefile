# Copyright 2014-2020 The Ideal Authors. All rights reserved.
#
# Use of this source code is governed by a BSD-style
# license that can be found in the LICENSE file or at
# https://developers.google.com/open-source/licenses/bsd

THIRD_PARTY_DIR = thirdparty

JDK_DIR = $(THIRD_PARTY_DIR)/jdk
JDK_VERSION = 1.8

BUILD_DIR = build
CLASSES_DIR = $(BUILD_DIR)/classes
TARGETS_DIR = $(BUILD_DIR)/targets
GENERATED_DIR = $(BUILD_DIR)/generated
JAVADOC_DIR = $(BUILD_DIR)/javadoc
PRETTY_DIR = $(BUILD_DIR)/pretty
SCRATCH_DIR = $(BUILD_DIR)/scratch

MKDIR = mkdir -p

JSR305_JAR = $(THIRD_PARTY_DIR)/jsr305-3.0.2.jar
JUNIT_JAR = $(THIRD_PARTY_DIR)/junit-4.13.jar
JAVACUP_JAR = $(THIRD_PARTY_DIR)/java-cup-11b.jar

THIRD_PARTY_JARS = $(JSR305_JAR):$(JUNIT_JAR):$(JAVACUP_JAR)

BOOTSTRAPPED_DIR = bootstrapped

JAVA = $(JDK_DIR)/bin/java -ea -classpath $(CLASSES_DIR):$(THIRD_PARTY_JARS)
JAVAC_SOURCE_OPTS = -source $(JDK_VERSION) -target $(JDK_VERSION) -Xlint:deprecation
JAVAC_OPTS = $(JAVAC_SOURCE_OPTS) \
        -classpath $(CLASSES_DIR):$(THIRD_PARTY_JARS) -d $(CLASSES_DIR) \
	-sourcepath .:$(GENERATED_DIR):$(BOOTSTRAPPED_DIR)
JAVAC = $(JDK_DIR)/bin/javac $(JAVAC_OPTS)
JAVAC_HERMETIC_OPTS = $(JAVAC_SOURCE_OPTS) -classpath $(THIRD_PARTY_JARS) -d $(CLASSES_DIR)
JAVAC_HERMETIC = $(JDK_DIR)/bin/javac $(JAVAC_HERMETIC_OPTS)
JAVAC_LINT_OPT = -Xlint:unchecked
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
CREATE = $(JAVA) $(CREATE_MAIN) -top=isource

FLAGS_RUN = -debug-progress -run

ISOURCE_DIR = isource
IDEAL_SOURCE = $(ISOURCE_DIR)/ideal.i
LIBRARY_ELEMENTS = $(ISOURCE_DIR)/library/elements.i
ONETWO = $(ISOURCE_DIR)/tests/12.i
ONE = $(ISOURCE_DIR)/tests/1.i
DIRECTORY = $(ISOURCE_DIR)/tests/directory.i
CIRCLE = $(ISOURCE_DIR)/showcase/circle.i
HELLO = $(ISOURCE_DIR)/showcase/hello.i
# TODO: deprecate ISOURCES
ISOURCES = $(ISOURCE_DIR)/*

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
    ideal/development/targets/*.java \
    ideal/development/parsers/*.java \
    ideal/development/documenters/*.java \
    ideal/development/tools/*.java \
    ideal/development/tests/*.java

default: print_elements

analyze_library: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=analyze_library

analyze_all: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=analyze_all

print_elements: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=print_elements

generate_library: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=generate_library

bootstrap_library: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_library -output=$(BOOTSTRAPPED_DIR)

test_library: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_library -output=$(SCRATCH_DIR)
	$(JAVAC) $(SCRATCH_DIR)/ideal/library/*/*java

### Running sample code

1: $(IDEAL_TARGET) $(ONE)
	$(CREATE) $(FLAGS_RUN) -input=$(ONE)

12: $(IDEAL_TARGET) $(ONETWO)
	$(CREATE) $(FLAGS_RUN) -input=$(ONETWO)

dir: $(IDEAL_TARGET) $(ONETWO)
	$(CREATE) $(FLAGS_RUN) -input=$(DIRECTORY)

circle: $(IDEAL_TARGET) $(CIRCLE)
	$(CREATE) $(FLAGS_RUN) -input=$(CIRCLE)

### Generating runtime

generate_runtime: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=generate_runtime

test_runtime: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_all -output=$(SCRATCH_DIR)
	$(MKDIR) $(SCRATCH_DIR)/ideal/machine
	cp -r ideal/machine/* $(SCRATCH_DIR)/ideal/machine
	$(JAVAC_HERMETIC) $(SCRATCH_DIR)/ideal/*/*/*java

bootstrap_runtime: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_runtime -output=$(BOOTSTRAPPED_DIR)

### Generating other

generate_all: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_all -output=$(SCRATCH_DIR)
	$(JAVAC) $(SCRATCH_DIR)/ideal/*/*/*java

reboot:
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_all -output=$(BOOTSTRAPPED_DIR)

generate_texts: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_texts

test_texts: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_texts -output=$(SCRATCH_DIR)
	$(JAVAC) $(SCRATCH_DIR)/ideal/runtime/texts/*java

bootstrap_texts: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_texts -output=$(BOOTSTRAPPED_DIR)

### Reflections runtime

generate_reflections: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_reflections

test_reflections: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_reflections -output=$(SCRATCH_DIR)
	$(JAVAC) $(SCRATCH_DIR)/ideal/runtime/reflections/*java

bootstrap_reflections: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_reflections -output=$(BOOTSTRAPPED_DIR)

### Documentation generation

document_elements: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=document_elements \
            -output=$(PRETTY_DIR)

document_library: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=document_library \
            -output=$(PRETTY_DIR)

document_runtime: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=document_runtime \
            -output=$(PRETTY_DIR)

document_all: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=document_all \
            -output=$(PRETTY_DIR)

### Other targets

import: $(IDEAL_TARGET)
	$(CREATE) -debug-import

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

buildall: $(IDEAL_TARGET)
	cd experimental/coach ; make

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

# Targets for profiling
AGENT_PATH = $(THIRD_PARTY_DIR)/yjp-12.0.6/bin/linux-x86-32/libyjpagent.so
JAVA_PROFILING_OPT = -agentpath:$(AGENT_PATH)=tracing
JAVA_PROFILING_OPT2 = -agentlib:hprof=cpu=times
CREATE_PROF = $(JAVA) $(JAVA_PROFILING_OPT) $(CREATE_MAIN)

12p: $(IDEAL_TARGET) $(ONETWO)
	$(CREATE_PROF) $(FLAGS_RUN) -input=$(ONETWO)

runtimep: $(IDEAL_TARGET)
	$(CREATE_PROF) -input=$(IDEAL_SOURCE) -target=generate_runtime > /dev/null
