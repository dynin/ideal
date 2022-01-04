-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Output channel adapter that counts the elements that pass through it.
class output_counter[any value value_type] {
  implements output[value_type];

  private output[value_type] the_output;
  private var integer count;

  output_counter(output[value_type] the_output) {
    this.the_output = the_output;
    this.count = 0;
  }

  integer get_count() {
    return count;
  }

  override write(value_type value) {
    count += 1;
    the_output.write(value);
  }

  override write_all(readonly list[value_type] values) {
    count += values.size;
    the_output.write_all(values);
  }

  override sync() {
    the_output.sync();
  }

  override close() {
    the_output.close();
  }
}
