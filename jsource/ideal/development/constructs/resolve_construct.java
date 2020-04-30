/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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
  public final construct name;
  public resolve_construct(construct qualifier, construct name, position pos) {
    super(pos);
    this.qualifier = qualifier;
    this.name = name;
  }

  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();

    result.append(qualifier);
    result.append(name);

    return result;
  }

  @Override
  public string to_string() {
    if (name instanceof stringable) {
      return utilities.describe(this, (stringable) name);
    } else {
      return super.to_string();
    }
  }
}
