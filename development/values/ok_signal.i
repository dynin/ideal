-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

-- TODO: use singleton.
class ok_signal {
  extends debuggable;
  implements signal;

  static ok_signal instance : ok_signal.new();

  implement origin deeper_origin => origin_utilities.no_origin;

  implement action to_action => common_values.nothing(this);

  implement string to_string => "ok";
}
