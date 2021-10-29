-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class data_value_action[readonly data_value value_type] {
  extends base_value_action[value_type];

  data_value_action(value_type the_value, origin the_origin) {
    super(the_value, the_origin);
  }

  -- TODO: this is redundant.
  private var base_value_action[data_value] get_value_action() {
    return this !> base_value_action[data_value];
  }

  override abstract_value result => get_value_action.the_value;

  override declaration or null get_declaration => get_value_action.get_declaration;
}
