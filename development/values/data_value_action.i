-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class data_value_action[readonly data_value[any value] value_type] {
  extends base_value_action[value_type];

  data_value_action(value_type the_value, origin the_origin) {
    super(the_value, the_origin);
  }

  readonly action as_action() pure {
    return this .> readonly action;
    --base_value_action[data_value[any value]];
  }

  override abstract_value result() {
    return as_action().result;
  }

  override declaration or null get_declaration => as_action().get_declaration;
}
