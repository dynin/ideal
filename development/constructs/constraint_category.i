-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

enum constraint_category {
  implements deeply_immutable data, stringable;

  ASSERT_CONSTRAINT("assert");
  VERIFY_CONSTRAINT("verify");

  private final string name_string;

  private constraint_category(string name_string) {
    this.name_string = name_string;
  }

  var simple_name constraint_name => simple_name.make(name_string);

  override string to_string() => name_string;
}
