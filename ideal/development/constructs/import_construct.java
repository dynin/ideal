/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
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
  public final construct type;
  public import_construct(readonly_list<annotation_construct> annotations,
		          construct type,
		          position pos) {
    super(pos);
    this.annotations = annotations;
    this.type = type;
  }

  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();

    do_append_all(result, annotations);
    result.append(type);

    return result;
  }

  // TODO: the need for this is Java-specific, move out of constructs package.
  public boolean has_implicit() {
    // TODO: use list.has()...
    for (int i = 0; i < annotations.size(); ++i) {
      annotation_construct the_annotation = annotations.get(i);
      if (the_annotation instanceof modifier_construct) {
        if (((modifier_construct) the_annotation).the_kind == general_modifier.implicit_modifier) {
          return true;
        }
      }
    }
    return false;
  }
}
