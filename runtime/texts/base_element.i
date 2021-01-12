-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Implementation of a text element with children.
class base_element {
  extends debuggable;
  implements text_element;

  private element_id id;
  private immutable dictionary[attribute_id, attribute_fragment] the_attributes;
  private text_fragment or null the_children;

  overload base_element(element_id id) {
    -- TODO: Implement empty dictionary
    this(id, list_dictionary[attribute_id, attribute_fragment].new(), missing.instance);
  }

  public overload base_element(element_id id,
      readonly dictionary[attribute_id, attribute_fragment] attributes,
      text_fragment or null children) {
    this.id = id;
    this.the_attributes = attributes.frozen_copy();
    this.the_children = children;
  }

  static overload text_element make(element_id id, text_fragment or null children) {
    return base_element.new(id, list_dictionary[attribute_id, attribute_fragment].new(), children);
  }

  static overload text_element make(element_id id, attribute_id attr,
      attribute_fragment value, text_fragment or null children) {
    return base_element.new(id, list_dictionary[attribute_id, attribute_fragment].new(attr, value),
        children);
  }

  override element_id get_id => id;

  override immutable dictionary[attribute_id, attribute_fragment] attributes => the_attributes;

  override immutable text_fragment or null children => the_children;

  override string to_string() {
    return "<" ++ id.to_string ++ " ...>";
  }
}
