-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Flags for the create applications.
meta_flags class create_flags {
  string or null top;
  string or null input;
  string or null output;
  string or null #id:target;

  boolean RUN;
  boolean PRINT;
  boolean PRETTY_PRINT;
  boolean GENERATE;

  boolean UNIT_TESTS;

  boolean CURE_UNDECLARED;
  boolean HIDE_DECLARATIONS;
  boolean SLOW_MODE;

  boolean DEBUG_CONSTRUCTS;
  boolean DEBUG_ACTIONS;
  boolean DEBUG_PROGRESS;
  boolean DEBUG_IMPORT;
  boolean DEBUG_REFLECT;
}
