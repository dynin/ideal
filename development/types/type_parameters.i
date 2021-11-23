-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.machine.channels.string_writer;

--- Encapsulate parameters used in |parametrized_type|.
-- TODO: this should be a type alias.
class type_parameters {
  implements deeply_immutable data;
  extends debuggable;

  immutable list[abstract_value] the_list;

  type_parameters(readonly list[abstract_value] the_list) {
    this.the_list = the_list.frozen_copy;
  }

  override string to_string() {
    the_writer : string_writer.new();
    the_writer.write_all("[");

    -- TODO: use list.join()
    for (var nonnegative i : 0; i < the_list.size; i += 1) {
      if (i > 0) {
        the_writer.write_all(", ");
      }
      parameter : the_list[i];
      var string name;
      -- TODO: this heuristic needs to be improved...
      if (parameter is base_type) { -- && type_utilities.is_type_alias((type) parameter)
        name = parameter.describe(type_format.TWO_PARENTS);
      } else {
        name = parameter.to_string;
      }
      the_writer.write_all(name);
    }

    the_writer.write_all("]");
    return the_writer.elements;
  }
}
