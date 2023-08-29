-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

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

  override write(value_type element) {
    assert active;
    the_list.append(element);
  }

  override write_all(readonly list[value_type] elements) {
    assert active;
    the_list.append_all(elements);
  }

  override sync() {
  }

  override close() {
    active = false;
  }

  override teardown() {
    active = false;
  }

  var immutable list[value_type] elements() {
    return the_list.elements;
  }

  clear() {
    the_list.clear();
  }
}
