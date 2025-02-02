-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

enum jump_category {
  BREAK_JUMP: new("break");
  CONTINUE_JUMP: new("continue");
  -- TODO: GOTO_JUMP etc...

  private final string name_string;

  private jump_category(string name_string) {
    this.name_string = name_string;
  }

  var simple_name jump_name => simple_name.make(name_string);

  override string to_string => name_string;
}
