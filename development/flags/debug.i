-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Debug flags for the create components.
namespace debug {
  var boolean PROGRESS;
  var boolean ACTIONS;
  var boolean CURE_UNDECLARED;
  var boolean HIDE_DECLARATIONS;
  var boolean RESOLVE : false; -- Currently unused.
  var boolean SUBTYPE : false; -- Currently unused.
  var boolean PROMOTION : false; -- Currently unused.
  var boolean FRAGMENTS : false;
  var boolean DO_REDUNDANT_CHECKS : true;

  void initialize(create_flags flags) {
    PROGRESS = flags.DEBUG_PROGRESS;
    ACTIONS = flags.DEBUG_ACTIONS;
    CURE_UNDECLARED = flags.CURE_UNDECLARED;
    HIDE_DECLARATIONS = flags.HIDE_DECLARATIONS;
  }
}
