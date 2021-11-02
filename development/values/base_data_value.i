-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class base_data_value {
  implements data_value;
  extends debuggable;

  private type bound;

  base_data_value(type bound) {
    this.bound = bound;
  }

  override type type_bound => bound;

  override any value unwrap => this;

  override action to_action(origin the_origin) =>
      data_value_action[data_value].new(this, the_origin);

  override boolean is_parametrizable => bound.is_parametrizable;

  declaration or null get_declaration => missing.instance;
}
