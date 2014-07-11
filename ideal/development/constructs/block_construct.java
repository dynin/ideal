/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class block_construct extends base_construct {
  public final readonly_list<annotation_construct> annotations;
  public final list<construct> body;

  public block_construct(readonly_list<annotation_construct> annotations,
      list<construct> body, position pos) {
    super(pos);
    this.annotations = annotations;
    this.body = body;
  }

  public block_construct(list<construct> body, position pos) {
    this(new empty<annotation_construct>(), body, pos);
  }

  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();
    do_append_all(result, annotations);
    result.append_all(body);
    return result;
  }
}
