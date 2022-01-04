-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

public class panic_value {
  extends jump_wrapper;

  string message;

  panic_value(string message) {
    this.message = message;
  }

  override string to_string => "panic value: " ++ message;
}
