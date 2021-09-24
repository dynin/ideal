-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.origins.*;
import ideal.development.types.*;
import javax.annotation.Nullable;

public class ok_signal extends debuggable implements signal {

  public static final ok_signal instance = new ok_signal();

  @Override
  public origin deeper_origin() {
    return origin_utilities.no_origin;
  }

  @Override
  public string to_string() {
    return new base_string("ok");
  }
}
