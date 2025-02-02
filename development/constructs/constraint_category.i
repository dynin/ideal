-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

enum constraint_category {
  ASSERT_CONSTRAINT: new("assert");
  VERIFY_CONSTRAINT: new("verify");

  private final string name_string;

  private constraint_category(string name_string) {
    this.name_string = name_string;
  }

  var simple_name constraint_name => simple_name.make(name_string);

  override string to_string() => name_string;
}
