/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class resolve_construct extends base_construct {
  public final construct qualifier;
  public action_name the_name;

  public resolve_construct(construct qualifier, action_name the_name, origin pos) {
    super(pos);
    assert qualifier != null;
    assert the_name != null;
    this.qualifier = qualifier;
    this.the_name = the_name;
  }

  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();

    result.append(qualifier);

    return result;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_name);
  }
}
