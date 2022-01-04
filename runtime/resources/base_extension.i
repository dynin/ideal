-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- A catalog of extensions.
enum base_extension {
  implements extension;

  HTML: new("html");
  IDEAL_SOURCE: new("i");
  CSS: new("css");
  TEXT: new("txt");
  JAVA_SOURCE: new("java");
  JAVASCRIPT_SOURCE: new("js");
  JSON: new("json");

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
