-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- An adapter that creates an output channel backed by a list.
class appender[any value value_type] {
  implements output[value_type];

  private list[value_type] the_list;
  private var boolean active;

  overload appender(list[value_type] the_list) {
    this.the_list = the_list;
    this.active = true;
  }

  overload appender() {
    this(base_list[value_type].new());
  }

  override void write(value_type element) {
    assert active;
    the_list.append(element);
  }

  override void write_all(readonly list[value_type] elements) {
    assert active;
    the_list.append_all(elements);
  }

  override void sync() {
  }

  override void close() {
    active = false;
  }

  immutable list[value_type] elements() {
    return the_list.elements;
  }

  --- Get elements and clear the state.
  immutable list[value_type] extract_elements() {
    result : the_list.elements;
    the_list.clear();
    return result;
  }
}
