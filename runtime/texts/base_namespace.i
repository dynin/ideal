-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Namespace used to avoid conflicts in |element_id|s and |attrubite_id|s.
class base_namespace {
  extends debuggable;
  implements text_namespace;
  implements reference_equality;

  private string name;

  base_namespace(string name) {
    this.name = name;
  }

  override string short_name() {
    return name;
  }

  override string to_string() {
    return name;
  }
}
