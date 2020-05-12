-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- An event indicating the start of a structured text element.
class start_element {
  extends debuggable;
  implements text_event;

  private final element_id id;

  start_element(element_id id) {
    this.id = id;
  }

  element_id get_id => id;

  override string to_string => base_string.new("<", id.to_string, ">");
}
