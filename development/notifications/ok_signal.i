-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

-- TODO: use singleton.
class ok_signal {
  extends debuggable;
  implements signal;

  static ok_signal instance : ok_signal.new();

  override origin deeper_origin => origin_utilities.no_origin;

  override string to_string => "ok";
}
