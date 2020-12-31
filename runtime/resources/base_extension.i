-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A catalog of extensions.
enum base_extension {
  implements extension;

  HTML("html");
  IDEAL_SOURCE("i");
  CSS("css");
  TEXT("txt");
  JAVA_SOURCE("java");
  JAVASCRIPT_SOURCE("js");

  private string the_dot_name;

  private base_extension(string name) {
    the_dot_name = base_string.new(".", name);
  }

  override string dot_name() {
    return the_dot_name;
  }

  override string to_string() {
    return the_dot_name;
  }

  --override String toString() {
  --  return dot_name.s();
  --}
}
