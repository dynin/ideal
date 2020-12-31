/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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

public class list_construct extends base_construct {
  public final readonly_list<construct> elements;
  public final grouping_type grouping;
  public final boolean has_trailing_comma;

  public list_construct(readonly_list<construct> elements, grouping_type grouping,
      boolean has_trailing_comma, origin pos) {
    super(pos);

    assert elements != null;
    assert grouping != null;

    this.elements = elements;
    this.grouping = grouping;
    this.has_trailing_comma = has_trailing_comma;
  }

  /** Simple grouping, such as (foo) */
  public boolean is_simple_grouping() {
    return elements.size() == 1 && !has_trailing_comma;
  }

  public readonly_list<construct> children() {
    return elements;
  }
}
