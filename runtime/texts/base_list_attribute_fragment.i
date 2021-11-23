-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A list of attribute fragments that is an |attribute_fragment|.
class base_list_attribute_fragment {
  extends debuggable;
  implements list_attribute_fragment;

  import ideal.machine.channels.string_writer;

  private immutable list[attribute_fragment] the_fragments;

  public base_list_attribute_fragment(readonly list[attribute_fragment] the_fragments) {
    this.the_fragments = the_fragments.frozen_copy;
  }

  public overload static list_attribute_fragment make(attribute_fragment first,
      attribute_fragment second) {
    return base_list_attribute_fragment.new([first, second]);
  }

  public overload static list_attribute_fragment make(attribute_fragment first,
      attribute_fragment second, attribute_fragment third) {
    return base_list_attribute_fragment.new([first, second, third]);
  }

  override immutable list[attribute_fragment] fragments() {
    return the_fragments;
  }

  override string to_string() {
    the_writer : string_writer.new();
    for (node : the_fragments) {
      the_writer.write_all(node.to_string);
    }
    return the_writer.elements;
  }
}
