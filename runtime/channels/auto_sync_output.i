-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Output channel adapter that performs syncs (aka flush) after every write.
class auto_sync_output[any value value_type] {
  implements output[value_type];

  private output[value_type] the_output;

  auto_sync_output(output[value_type] the_output) {
    this.the_output = the_output;
  }

  implement write(value_type value) {
    the_output.write(value);
    the_output.sync();
  }

  implement write_all(readonly list[value_type] values) {
    the_output.write_all(values);
    the_output.sync();
  }

  implement sync() {
    the_output.sync();
  }

  implement close() {
    the_output.close();
  }
}
