-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Attribute identifiers in markup languages, such as <code>href</code>.
class base_attribute_id {
  extends debuggable;
  implements attribute_id;
  implements reference_equality;

  private text_namespace the_namespace;
  private string name;

  base_attribute_id(text_namespace the_namespace, string name) {
    this.the_namespace = the_namespace;
    this.name = name;
  }

  override text_namespace get_namespace() {
    return the_namespace;
  }

  override string short_name() {
    return name;
  }

  override string to_string() {
    return the_namespace.to_string ++ ":" ++ name;
  }
}
