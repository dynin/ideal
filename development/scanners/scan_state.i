-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

auto_constructor class scan_state {
  implements deeply_immutable data;

  token token;
  nonnegative prefix_end;
  nonnegative end;

  -- TODO: the sign value.
  -- TODO: factor out into an order implementation.
  integer compare_to(scan_state other) {
    var integer result : this.prefix_end - other.prefix_end;
    if (result == 0) {
      result = this.end - other.end;
    }
    return result;
  }
}
