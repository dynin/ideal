-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class parameter_construct extends base_construct {
  public final construct main;
  public final readonly_list<construct> parameters;
  public final grouping_type grouping;

  public parameter_construct(construct main, readonly_list<construct> parameters,
      grouping_type grouping, origin pos) {
    super(pos);

    assert main != null;
    assert parameters != null;
    assert grouping != null;

    this.main = main;
    this.parameters = parameters;
    this.grouping = grouping;
  }

  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();

    result.append(main);
    result.append_all(parameters);

    return result;
  }
}
