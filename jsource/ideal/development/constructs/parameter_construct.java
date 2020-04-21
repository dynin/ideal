/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class parameter_construct extends base_construct {
  public final construct main;
  public final list_construct parameters;

  public parameter_construct(construct main, list_construct parameters, position pos) {
    super(pos);

    assert main != null;
    assert parameters != null;

    this.main = main;
    this.parameters = parameters;
  }

  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();

    result.append(main);
    result.append(parameters);

    return result;
  }
}
