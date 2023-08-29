-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Output channel adapter that uses a function to perform a mapping.
class output_transformer[any value source_type, any value destination_type] {
  implements output[source_type];

  private function[destination_type, source_type] the_function;
  private output[destination_type] the_output;

  output_transformer(function[destination_type, source_type] the_function,
                     output[destination_type] the_output) {
    this.the_function = the_function;
    this.the_output = the_output;
  }

  override write(source_type value) {
    the_output.write(the_function(value));
  }

  -- Note: the contract for write_all() specifies that the values are written as a block
  -- even in the precense of multiple concurrent writers.
  override write_all(readonly list[source_type] values) {
    -- TODO: use list.map()
    transformed_values : base_list[destination_type].new();
    for (value : values) {
      transformed_values.append(the_function(value));
    }
    the_output.write_all(transformed_values);
  }

  override sync() {
    the_output.sync();
  }

  override close() {
    the_output.close();
  }

  override teardown() {
    the_output.teardown();
  }
}
