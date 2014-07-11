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

public class variable_construct extends base_construct {
  public final readonly_list<annotation_construct> annotations;
  public final @Nullable construct type;
  public final action_name name;
  public final readonly_list<annotation_construct> post_annotations;
  public final @Nullable construct init;

  public variable_construct(readonly_list<annotation_construct> annotations,
		       @Nullable construct type,
                       action_name name,
                       readonly_list<annotation_construct> post_annotations,
		       @Nullable construct init,
		       position pos) {
    super(pos);
    this.annotations = annotations;
    this.type = type;
    assert name != null;
    this.name = name;
    this.post_annotations = post_annotations;
    this.init = init;
  }

  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();

    do_append_all(result, annotations);
    if (type != null) {
      result.append(type);
    }
    if (init != null) {
      result.append(init);
    }

    return result;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, name);
  }
}
