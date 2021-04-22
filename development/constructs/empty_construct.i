-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class empty_construct extends base_construct {
  public empty_construct(origin pos) {
    super(pos);
  }

  public readonly_list<construct> children() {
    return new empty<construct>();
  }
}
