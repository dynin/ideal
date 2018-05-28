-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Implementation of a text element with children.
class base_element {
  extends debuggable;
  implements text_element;

  private text_id id;
  private immutable list[text_node] the_children;

  base_element(text_id id) {
    this.id = id;
    this.the_children = empty[text_node].new();
  }

  public base_element(text_id id, readonly list[text_node] or null the_children) {
    this.id = id;
    this.the_children = the_children is_not null ?
        the_children.frozen_copy() : empty[text_node].new();
  }

  static overload text_element make(text_id id, text_fragment or null fragment) {
    return base_element.new(id, text_util.to_list(fragment));
  }

  static overload text_element make(element_id id, attribute_id attr,
      string value, text_fragment or null fragment) {
    children : base_list[text_node].new();
    children.append(base_element.new(attr, base_list[text_node].new(value as base_string)));
    children.append_all(text_util.to_list(fragment));
    return base_element.new(id, children);
  }

  override text_id get_id() {
    return id;
  }

  override immutable list[text_node] children() {
    return the_children;
  }

  override string to_string() {
    return "<" ++ id.to_string ++ " ...>";
  }
}
