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
import ideal.development.modifiers.*;

public class import_construct extends base_construct {
  public final readonly_list<annotation_construct> annotations;
  public final construct type_construct;
  public import_construct(readonly_list<annotation_construct> annotations,
                          construct type,
                          origin pos) {
    super(pos);
    this.annotations = annotations;
    this.type_construct = type;
  }

  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();

    do_append_all(result, annotations);
    result.append(type_construct);

    return result;
  }
}
