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

JSOURCE_DIR = jsource
BOOTSTRAPPED_DIR = bootstrapped

JAVA = $(JDK_DIR)/bin/java -ea -classpath $(CLASSES_DIR):$(THIRD_PARTY_JARS)
JAVAC_SOURCE_OPTS = -source $(JDK_VERSION) -target $(JDK_VERSION) -Xlint:deprecation
JAVAC_OPTS = $(JAVAC_SOURCE_OPTS) \
        -classpath $(CLASSES_DIR):$(THIRD_PARTY_JARS) -d $(CLASSES_DIR) \
	-sourcepath $(JSOURCE_DIR):$(GENERATED_DIR):$(BOOTSTRAPPED_DIR)
JAVAC = $(JDK_DIR)/bin/javac $(JAVAC_OPTS)
JAVAC_HERMETIC_OPTS = $(JAVAC_SOURCE_OPTS) -classpath $(THIRD_PARTY_JARS) -d $(CLASSES_DIR)
JAVAC_HERMETIC = $(JDK_DIR)/bin/javac $(JAVAC_HERMETIC_OPTS)
JAVAC_LINT_OPT = -Xlint:unchecked
JAVADOC = $(JDK_DIR)/bin/javadoc -classpath $(THIRD_PARTY_JARS) \
        -sourcepath $(GENERATED_DIR) -d $(JAVADOC_DIR)

PARSER_DIR = $(GENERATED_DIR)/ideal/development/symbols
PARSER2SRC_DIR = ../../../../..
JAVACUP = $(PARSER2SRC_DIR)/$(JDK_DIR)/bin/java \
        -classpath $(PARSER2SRC_DIR)/$(JAVACUP_JAR) java_cup.Main

BOOTSTRAPPED_TARGET = $(TARGETS_DIR)/boostrapped
LIBRARY_TARGET = $(TARGETS_DIR)/library
DEVELOPMENT_TARGET = $(TARGETS_DIR)/development
BASEPARSER_TARGET = $(TARGETS_DIR)/baseparser
IDEAL_TARGET = $(TARGETS_DIR)/ideal

CREATE_MAIN = ideal.development.tools.create
CREATE = $(JAVA) $(CREATE_MAIN)
IDEAL_SOURCE = ideal.i

FLAGS_RUN = -debug-progress -run

TEST_DIR = tests
ONETWO = $(TEST_DIR)/12.i
ONE = $(TEST_DIR)/1.i
DIRECTORY = $(TEST_DIR)/directory.i

SHOWCASE_DIR = showcase
CIRCLE = $(SHOWCASE_DIR)/circle.i
HELLO = $(SHOWCASE_DIR)/hello.i

BOOTSTRAPPED_JAVA = \
    $(BOOTSTRAPPED_DIR)/ideal/library/elements/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/library/channels/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/library/characters/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/library/texts/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/library/resources/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/library/patterns/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/library/messages/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/library/reflections/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/library/graphs/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/elements/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/patterns/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/texts/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/reflections/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/channels/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/resources/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/graphs/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/logs/*.java

MACHINE_JAVA = \
    $(JSOURCE_DIR)/ideal/machine/elements/*.java \
    $(JSOURCE_DIR)/ideal/machine/annotations/*.java \
    $(JSOURCE_DIR)/ideal/machine/channels/*.java \
    $(JSOURCE_DIR)/ideal/machine/characters/*.java \
    $(JSOURCE_DIR)/ideal/machine/resources/*.java

PARSER_CUP = $(JSOURCE_DIR)/ideal/development/parsers/base_parser.cup

BOOTSTRAPPED_DEVELOPMENT = \
    $(BOOTSTRAPPED_DIR)/ideal/development/elements/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/texts/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/names/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/components/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/comments/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/flavors/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/modifiers/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/declarations/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/kinds/*.java

DEVELOPMENT_JAVA = \
    $(JSOURCE_DIR)/ideal/development/constructs/*.java \
    $(JSOURCE_DIR)/ideal/development/types/*.java \
    $(JSOURCE_DIR)/ideal/development/literals/*.java \
    \
    $(JSOURCE_DIR)/ideal/development/actions/*.java \
    $(JSOURCE_DIR)/ideal/development/notifications/*.java \
    $(JSOURCE_DIR)/ideal/development/values/*.java \
    $(JSOURCE_DIR)/ideal/development/functions/*.java \
    $(JSOURCE_DIR)/ideal/development/scanners/*.java \
    $(JSOURCE_DIR)/ideal/development/analyzers/*.java \
    $(JSOURCE_DIR)/ideal/development/templates/*.java \
    $(JSOURCE_DIR)/ideal/development/transformers/*.java \
    $(JSOURCE_DIR)/ideal/development/printers/*.java \
    $(JSOURCE_DIR)/ideal/development/extensions/*.java \
    $(JSOURCE_DIR)/ideal/development/targets/*.java \
    $(JSOURCE_DIR)/ideal/development/parsers/*.java \
    $(JSOURCE_DIR)/ideal/development/documenters/*.java \
    $(JSOURCE_DIR)/ideal/development/tools/*.java \
    $(JSOURCE_DIR)/ideal/development/tests/*.java

default: print_elements

analyze_library: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=analyze_library

analyze_runtime: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=analyze_runtime

analyze_all: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=analyze_all

print_elements: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=print_elements

generate_library: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=generate_library

generate_library2: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=generate_library2

bootstrap_library: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_library -output=$(BOOTSTRAPPED_DIR)

test_library: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_library -output=$(SCRATCH_DIR)
	$(JAVAC) $(SCRATCH_DIR)/ideal/library/*/*java

t2: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_library2 -output=$(SCRATCH_DIR)
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

bootstrap_runtime: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_runtime -output=$(BOOTSTRAPPED_DIR)

ga: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_array

### Generating other

generate_all: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_all -output=$(SCRATCH_DIR)
	$(JAVAC) $(SCRATCH_DIR)/ideal/*/*/*java

test_all: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=generate_all -output=$(SCRATCH_DIR)
	$(MKDIR) $(SCRATCH_DIR)/ideal/machine
	cp -r $(JSOURCE_DIR)/ideal/machine/* $(SCRATCH_DIR)/ideal/machine
	$(JAVAC_HERMETIC) $(SCRATCH_DIR)/ideal/*/*/*java

bbackup:
	tar cfz tmp/bb-`date "+%H-%M-%S"`.tgz $(BOOTSTRAPPED_DIR) $(JSOURCE_DIR)

reboot:
	tar cfz tmp/bb-`date "+%H-%M-%S"`.tgz $(BOOTSTRAPPED_DIR) $(JSOURCE_DIR)
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

### Development

generate_development gd: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_development

test_development td: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_development -output=$(SCRATCH_DIR)
	$(JAVAC) $(SCRATCH_DIR)/ideal/development/*/*java

tdc:
	$(JAVAC) $(SCRATCH_DIR)/ideal/development/*/*java

bootstrap_development bootdev devboot: $(IDEAL_TARGET)
	tar cfz tmp/bb-`date "+%H-%M-%S"`.tgz $(BOOTSTRAPPED_DIR) $(JSOURCE_DIR)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_development -output=$(BOOTSTRAPPED_DIR)

print_declarations: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=print_declarations

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

coach: $(IDEAL_TARGET)
	make -C experimental/coach coach

runserver: $(IDEAL_TARGET)
	make -C experimental/coach runserver

hello: $(IDEAL_TARGET)
	$(CREATE) $(FLAGS_RUN) -input=$(HELLO)

diff: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=print_elements | diff -B -E - library/elements.i

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

$(DEVELOPMENT_TARGET): $(BOOTSTRAPPED_DEVELOPMENT) $(LIBRARY_TARGET)
	$(JAVAC) $(BOOTSTRAPPED_DEVELOPMENT)
	@touch $@
	@echo === Development done.

$(BASEPARSER_TARGET): $(PARSER_CUP)
	$(MKDIR) $(PARSER_DIR)
	cd $(PARSER_DIR) ; \
	  $(JAVACUP) \
	      -package ideal.development.symbols \
	      -parser base_parser \
	      -symbols base_symbols \
	      $(PARSER2SRC_DIR)/$(PARSER_CUP)
	bin/process-base-symbols.sh \
            < $(PARSER_DIR)/base_symbols.java \
            > development/symbols/base_symbols.i
	@touch $@
	@echo === Parser done.

$(IDEAL_TARGET): build $(DEVELOPMENT_JAVA) $(LIBRARY_TARGET) $(DEVELOPMENT_TARGET) \
          $(BASEPARSER_TARGET) Makefile
	$(JAVAC) $(DEVELOPMENT_JAVA)
	@touch $@
	@echo === ideal done.

buildall: $(IDEAL_TARGET)
	cd experimental/coach ; make

jdoc: $(IDEAL_TARGET)
	$(MKDIR) $(JAVADOC_DIR)
	$(JAVADOC) $(BOOTSTRAPPED_JAVA) $(MACHINE_JAVA) $(BOOTSTRAPPED_DEVELOPMENT) \
              $(DEVELOPMENT_JAVA)

grammar:
	cd $(PARSER_DIR) ; \
	  $(JAVACUP) -dump_grammar \
	      -package ideal.development.symbols \
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

wc:
	wc -l `find library runtime  development -name \*i`

# Targets for profiling
AGENT_PATH = $(THIRD_PARTY_DIR)/yjp-12.0.6/bin/linux-x86-32/libyjpagent.so
JAVA_PROFILING_OPT = -agentpath:$(AGENT_PATH)=tracing
JAVA_PROFILING_OPT2 = -agentlib:hprof=cpu=times
CREATE_PROF = $(JAVA) $(JAVA_PROFILING_OPT) $(CREATE_MAIN)

12p: $(IDEAL_TARGET) $(ONETWO)
	$(CREATE_PROF) $(FLAGS_RUN) -input=$(ONETWO)

runtimep: $(IDEAL_TARGET)
	$(CREATE_PROF) -input=$(IDEAL_SOURCE) -target=generate_runtime > /dev/null
