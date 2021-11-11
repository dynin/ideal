# Copyright 2014-2021 The Ideal Authors. All rights reserved.
#
# Use of this source code is governed by a BSD-style
# license that can be found in the LICENSE file or at
# https://developers.google.com/open-source/licenses/bsd

THIRD_PARTY_DIR = thirdparty

JDK_DIR = $(THIRD_PARTY_DIR)/jdk

BUILD_DIR = build
CLASSES_DIR = $(BUILD_DIR)/classes
CLASSES_HERMETIC_DIR = $(BUILD_DIR)/hermetic
TARGETS_DIR = $(BUILD_DIR)/targets
GENERATED_DIR = $(BUILD_DIR)/generated
JAVADOC_DIR = $(BUILD_DIR)/javadoc
PRETTY_DIR = $(BUILD_DIR)/pretty
SITE_DIR = $(BUILD_DIR)/theideal.org
SCRATCH_DIR = $(BUILD_DIR)/scratch

MKDIR = mkdir -p

JSR305_JAR = $(THIRD_PARTY_DIR)/jsr305-3.0.2.jar
JAVACUP_JAR = $(THIRD_PARTY_DIR)/java-cup-11b.jar

THIRD_PARTY_JARS = $(JSR305_JAR):$(JAVACUP_JAR)

JSOURCE_DIR = jsource
BOOTSTRAPPED_DIR = bootstrapped

JAVA = $(JDK_DIR)/bin/java -ea -classpath $(CLASSES_DIR):$(THIRD_PARTY_JARS)
JAVAC_SOURCE_OPTS = -Xlint:deprecation
JAVAC_OPTS = $(JAVAC_SOURCE_OPTS) \
        -classpath $(CLASSES_DIR):$(THIRD_PARTY_JARS) -d $(CLASSES_DIR) \
	-sourcepath $(JSOURCE_DIR):$(GENERATED_DIR):$(BOOTSTRAPPED_DIR)
JAVAC = $(JDK_DIR)/bin/javac $(JAVAC_OPTS)

JAVA_HERMETIC = $(JDK_DIR)/bin/java -ea -classpath $(CLASSES_HERMETIC_DIR):$(THIRD_PARTY_JARS)
JAVAC_HERMETIC_OPTS = $(JAVAC_SOURCE_OPTS) -classpath $(THIRD_PARTY_JARS) -d $(CLASSES_HERMETIC_DIR)
JAVAC_HERMETIC = $(JDK_DIR)/bin/javac $(JAVAC_HERMETIC_OPTS)
JAVAC_LINT_OPT = -Xlint:unchecked

JAVADOC = $(JDK_DIR)/bin/javadoc -classpath $(THIRD_PARTY_JARS) \
        -sourcepath $(GENERATED_DIR) -d $(JAVADOC_DIR)

PARSER_DIR = $(GENERATED_DIR)/ideal/development/symbols
PARSER2SRC_DIR = ../../../../..
JAVACUP = $(PARSER2SRC_DIR)/$(JDK_DIR)/bin/java \
        -classpath $(PARSER2SRC_DIR)/$(JAVACUP_JAR) java_cup.Main

BOOTSTRAPPED_TARGET = $(TARGETS_DIR)/bootstrapped
LIBRARY_TARGET = $(TARGETS_DIR)/library
DEVELOPMENT_TARGET = $(TARGETS_DIR)/development
BASEPARSER_TARGET = $(TARGETS_DIR)/baseparser
IDEAL_TARGET = $(TARGETS_DIR)/ideal

CREATE_MAIN = ideal.development.tools.create
CREATE = $(JAVA) $(CREATE_MAIN)
IDEAL_SOURCE = ideal.i

FLAGS_RUN = -run
FLAGS_RUN_PROGRESS = -run -debug-progress

TEST_DIR = tests
ONETWO = $(TEST_DIR)/12.i
ONE = $(TEST_DIR)/1.i
VAR = $(TEST_DIR)/var.i
TESTCACHE = $(TEST_DIR)/cache.i
DIRECTORY = $(TEST_DIR)/directory.i
TEST_STRING = $(TEST_DIR)/string.i
TEST_LIST = $(TEST_DIR)/list.i

SHOWCASE_DIR = showcase
CIRCLE = $(SHOWCASE_DIR)/circle.i
HELLO = $(SHOWCASE_DIR)/hello.i
XREFTEST = $(SHOWCASE_DIR)/xreftest.i
TESTPARSER = $(SHOWCASE_DIR)/testparser.i

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
    $(BOOTSTRAPPED_DIR)/ideal/library/calendars/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/elements/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/patterns/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/texts/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/reflections/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/channels/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/resources/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/graphs/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/calendars/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/logs/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/flags/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/runtime/tests/*.java

MACHINE_JAVA = \
    $(JSOURCE_DIR)/ideal/machine/elements/*.java \
    $(JSOURCE_DIR)/ideal/machine/annotations/*.java \
    $(JSOURCE_DIR)/ideal/machine/channels/*.java \
    $(JSOURCE_DIR)/ideal/machine/characters/*.java \
    $(JSOURCE_DIR)/ideal/machine/resources/*.java \
    $(JSOURCE_DIR)/ideal/machine/calendars/*.java

PARSER_CUP = $(JSOURCE_DIR)/ideal/development/parsers/base_parser.cup

BOOTSTRAPPED_DEVELOPMENT = \
    $(BOOTSTRAPPED_DIR)/ideal/development/elements/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/names/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/components/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/comments/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/flavors/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/modifiers/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/declarations/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/kinds/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/origins/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/documenters/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/jumps/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/constructs/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/flags/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/tests/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/types/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/notifications/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/values/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/languages/*.java \
    $(BOOTSTRAPPED_DIR)/ideal/development/literals/*.java

DEVELOPMENT_JAVA = \
    $(JSOURCE_DIR)/ideal/development/actions/*.java \
    $(JSOURCE_DIR)/ideal/development/policies/*.java \
    $(JSOURCE_DIR)/ideal/development/functions/*.java \
    $(JSOURCE_DIR)/ideal/development/scanners/*.java \
    $(JSOURCE_DIR)/ideal/development/analyzers/*.java \
    $(JSOURCE_DIR)/ideal/development/templates/*.java \
    $(JSOURCE_DIR)/ideal/development/transformers/*.java \
    $(JSOURCE_DIR)/ideal/development/printers/*.java \
    $(JSOURCE_DIR)/ideal/development/extensions/*.java \
    $(JSOURCE_DIR)/ideal/development/targets/*.java \
    $(JSOURCE_DIR)/ideal/development/parsers/*.java \
    $(JSOURCE_DIR)/ideal/development/tools/*.java

default: print_elements

analyze_library: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=analyze_library

analyze_runtime: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=analyze_runtime

analyze_all: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=analyze_all

time: $(IDEAL_TARGET)
	time $(CREATE) -slow-mode -input=$(IDEAL_SOURCE) -target=analyze_sources
	time $(CREATE) -input=$(IDEAL_SOURCE) -target=analyze_sources

print_elements: $(IDEAL_TARGET)
	$(CREATE) -debug-actions -debug-progress -input=$(IDEAL_SOURCE) -target=print_elements

generate_elements: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=generate_elements

generate_library: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=generate_library

bootstrap_library: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_library -output=$(BOOTSTRAPPED_DIR)

test_library: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_library -output=$(SCRATCH_DIR)
	$(JAVAC) $(SCRATCH_DIR)/ideal/library/*/*java

test_librun: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=generate_librun \
            -output=$(SCRATCH_DIR)
	$(MKDIR) $(SCRATCH_DIR)/ideal/machine
	cp -r $(JSOURCE_DIR)/ideal/machine/* $(SCRATCH_DIR)/ideal/machine
	$(JAVAC_HERMETIC) $(SCRATCH_DIR)/ideal/*/*/*java
	$(JAVA_HERMETIC) ideal.runtime.tests.run_tests

test_librun_run:
	$(JAVAC) $(SCRATCH_DIR)/ideal/*/*/*java
	$(JAVA) ideal.runtime.tests.run_tests

compile_runtime:
	$(JAVAC) $(SCRATCH_DIR)/ideal/*/*/*java

generate_cache: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=generate_cache

### Running sample code

1: $(IDEAL_TARGET) $(ONE)
	$(CREATE) $(FLAGS_RUN_PROGRESS) -input=$(ONE)

12: $(IDEAL_TARGET) $(ONETWO)
	$(CREATE) $(FLAGS_RUN_PROGRESS) -debug-constructs -input=$(ONETWO)

list: $(IDEAL_TARGET) $(TEST_LIST)
	$(CREATE) $(FLAGS_RUN_PROGRESS) -debug-constructs -input=$(TEST_LIST)

generate_var: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(VAR) -target=generate_var -output=$(SCRATCH_DIR)
	$(JAVAC) $(SCRATCH_DIR)/test/*java

reflect: $(IDEAL_TARGET)
	$(CREATE) $(FLAGS_RUN_PROGRESS) -debug-progress -debug-reflect -input=showcase/reflect.i

s: $(IDEAL_TARGET) $(TEST_STRING)
	$(CREATE) -debug-progress -input=$(TEST_STRING)

gt: $(IDEAL_TARGET)
	$(CREATE) -input=$(IDEAL_SOURCE) -target=generate_test

dir: $(IDEAL_TARGET) $(ONETWO)
	$(CREATE) $(FLAGS_RUN_PROGRESS) -input=$(DIRECTORY)

circle: $(IDEAL_TARGET) $(CIRCLE)
	$(CREATE) $(FLAGS_RUN_PROGRESS) -input=$(CIRCLE)

generate_showcase: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=generate_showcase \
            -output=$(SCRATCH_DIR)
	$(JAVAC) $(BOOTSTRAPPED_DIR)/ideal/library/*/*java \
                 $(BOOTSTRAPPED_DIR)/ideal/runtime/*/*java \
                 $(SCRATCH_DIR)/ideal/showcase/*java

run_briefing: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=generate_showcase \
            -output=$(SCRATCH_DIR)
	$(JAVAC) $(BOOTSTRAPPED_DIR)/ideal/library/*/*java \
                 $(BOOTSTRAPPED_DIR)/ideal/runtime/*/*java \
                 $(SCRATCH_DIR)/ideal/showcase/*java
	$(JAVA) ideal.showcase.briefing

xreftest: $(IDEAL_TARGET) $(XREFTEST)
	$(CREATE) -pretty-print -input=$(XREFTEST)

testparser: $(IDEAL_TARGET) $(TESTPARSER)
	$(CREATE) -generate -input=$(TESTPARSER) | tee $(GENERATED_DIR)/testparser.java
	$(JAVAC) $(GENERATED_DIR)/testparser.java

tc: $(IDEAL_TARGET) $(TESTCACHE)
	$(CREATE) $(FLAGS_RUN_PROGRESS) -input=$(TESTCACHE)

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
#	$(JAVA_HERMETIC) ideal.development.tools.create -unit-tests

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

.PHONY: rm-scratch

test_development td: $(IDEAL_TARGET) rm-scratch
	$(CREATE) -input=$(IDEAL_SOURCE) -debug-actions -target=generate_development \
            -output=$(SCRATCH_DIR)
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

document_librun: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=document_librun \
            -output=$(PRETTY_DIR)

document_development: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=document_development \
            -output=$(PRETTY_DIR)

document_all: $(IDEAL_TARGET)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=document_all \
            -output=$(PRETTY_DIR)

document_site: $(IDEAL_TARGET) rm-pretty
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=document_site \
            -output=$(PRETTY_DIR)

deploy_site: $(IDEAL_TARGET)
	rm -rf $(SITE_DIR)
	$(CREATE) -debug-progress -input=$(IDEAL_SOURCE) -target=deploy_site \
            -output=$(SITE_DIR)
	$(MKDIR) tmp
	cd $(BUILD_DIR) ; tar cvfz ../tmp/site.tgz theideal.org/*

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

.PHONY: test

test: $(IDEAL_TARGET)
	$(JAVA) ideal.development.tools.create -unit-tests

$(BOOTSTRAPPED_TARGET): $(BOOTSTRAPPED_JAVA)
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

$(IDEAL_TARGET): build $(DEVELOPMENT_JAVA) $(LIBRARY_TARGET) $(BASEPARSER_TARGET) \
          $(DEVELOPMENT_TARGET) Makefile
	$(JAVAC) $(DEVELOPMENT_JAVA)
	@touch $@
	@echo === ideal done.

buildall: $(IDEAL_TARGET)
#	cd experimental/coach ; make

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
	rm -f `find $(CLASSES_DIR) $(CLASSES_HERMETIC_DIR) -name \*.class`
	rm -f $(TARGETS_DIR)/*

rm-scratch:
	rm -rf $(SCRATCH_DIR)

rm-pretty:
	rm -rf $(PRETTY_DIR)

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
	$(CREATE_PROF) $(FLAGS_RUN_PROGRESS) -input=$(ONETWO)

runtimep: $(IDEAL_TARGET)
	$(CREATE_PROF) -input=$(IDEAL_SOURCE) -target=generate_runtime > /dev/null
