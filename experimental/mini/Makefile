# Copyright 2014-2025 The Ideal Authors. All rights reserved.
#
# Use of this source code is governed by a BSD-style
# license that can be found in the LICENSE file or at
# https://theideal.org/license/

THIRD_PARTY_DIR = ../../thirdparty

JDK_DIR = $(THIRD_PARTY_DIR)/jdk
JDK_VERSION = 1.8

BUILD_DIR = ../../build
CLASSES_DIR = $(BUILD_DIR)/classes
TARGETS_DIR = $(BUILD_DIR)/targets

JSR305_JAR = $(THIRD_PARTY_DIR)/jsr305-3.0.2.jar

MINI_DIR = ideal/experimental/mini

JAVA_MINI = $(JDK_DIR)/bin/java -cp $(CLASSES_DIR) -ea
JAVAC_MINI = $(JDK_DIR)/bin/javac $(JAVAC_SOURCE_OPTS) -Xlint:unchecked \
    -classpath $(JSR305_JAR) -d $(CLASSES_DIR)

MINI_CREATE_SOURCE = \
    $(MINI_DIR)/library.java \
    $(MINI_DIR)/bootstrapped.java \
    $(MINI_DIR)/feedback.java \
    $(MINI_DIR)/tokenizer.java \
    $(MINI_DIR)/parser.java \
    $(MINI_DIR)/analysis.java \
    $(MINI_DIR)/printer.java \
    $(MINI_DIR)/create.java
MINI_CREATE_TARGET = $(TARGETS_DIR)/mini-create
MINI_CREATE_MAIN = ideal.experimental.mini.create
JAVA_MINI_CREATE = $(JAVA_MINI) $(MINI_CREATE_MAIN)

MINI_HI_SOURCE = \
    $(MINI_DIR)/hi.java
MINI_HI_TARGET = $(TARGETS_DIR)/mini-hi
MINI_HI_MAIN = ideal.experimental.mini.hi
JAVA_MINI_HI = $(JAVA_MINI) $(MINI_HI_MAIN)

MINI_IDEAL_SOURCE = $(MINI_DIR)/bootstrapped.i
MINI_BOOTSTRAPPED = $(MINI_DIR)/bootstrapped.java
MINI_BOOTSTRAPPED_TMP = $(MINI_BOOTSTRAPPED).tmp
MINI_BOOTSTRAPPED_SAVE = $(MINI_BOOTSTRAPPED).save

$(MINI_CREATE_TARGET): $(MINI_CREATE_SOURCE)
	$(JAVAC_MINI) $^
	@touch $@

$(MINI_HI_TARGET): $(MINI_HI_SOURCE)
	$(JAVAC_MINI) $^
	@touch $@

m0: $(MINI_CREATE_TARGET)
	@$(JAVA_MINI_CREATE) $(MINI_DIR)/test.i

mini: $(MINI_CREATE_TARGET)
	@$(JAVA_MINI_CREATE) -analyze $(MINI_DIR)/test.i

test: $(MINI_CREATE_TARGET)
	@$(JAVA_MINI_CREATE) -analyze $(MINI_DIR)/test2.i

minia: $(MINI_CREATE_TARGET)
	@$(JAVA_MINI_CREATE) -analyze $(MINI_IDEAL_SOURCE)

minib: $(MINI_CREATE_TARGET)
	@cat $(MINI_DIR)/header.txt
	@$(JAVA_MINI_CREATE) $(MINI_IDEAL_SOURCE) | sed s'/^/  /'
	@echo }

minip: $(MINI_CREATE_TARGET)
	@echo Testing bootstrapping
	@cp $(MINI_BOOTSTRAPPED) $(MINI_BOOTSTRAPPED_SAVE)
	@make -s minib > $(MINI_BOOTSTRAPPED_TMP)
	@-mv $(MINI_BOOTSTRAPPED_TMP) $(MINI_BOOTSTRAPPED)
	@-make $(MINI_CREATE_TARGET)
	@-mv $(MINI_BOOTSTRAPPED_SAVE) $(MINI_BOOTSTRAPPED)

miniboot: $(MINI_CREATE_TARGET)
	@echo Bootstrapping $(MINI_BOOTSTRAPPED)
	@make -s minib > $(MINI_BOOTSTRAPPED_TMP)
	@mv $(MINI_BOOTSTRAPPED_TMP) $(MINI_BOOTSTRAPPED)

hi: $(MINI_HI_TARGET)
	@$(JAVA_MINI_HI)

hiswing: $(MINI_HI_TARGET)
	@$(JAVA_MINI) -Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel $(MINI_HI_MAIN)

wipeout:
	cd ../.. ; make wipeout
