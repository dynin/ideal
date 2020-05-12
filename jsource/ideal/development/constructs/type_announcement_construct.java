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

public class type_announcement_construct extends base_construct {

  public final readonly_list<annotation_construct> annotations;
  public final kind kind;
  public final action_name name;

  public type_announcement_construct(readonly_list<annotation_construct> annotations,
      kind kind, action_name name, origin pos) {
    super(pos);
    this.annotations = annotations;
    this.kind = kind;
    this.name = name;
  }

  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();
    do_append_all(result, annotations);
    return result;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, name);
  }
}
