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

public class procedure_construct extends base_construct {
  public final readonly_list<annotation_construct> annotations;
  public final @Nullable construct ret;
  public final action_name name;
  public final list_construct parameters;
  public final readonly_list<annotation_construct> post_annotations;
  public final @Nullable construct body;
  public procedure_construct(readonly_list<annotation_construct> annotations,
                            @Nullable construct ret,
                            action_name name,
                            list_construct parameters,
                            readonly_list<annotation_construct> post_annotations,
                            @Nullable construct body,
                            origin pos) {
    super(pos);
    this.annotations = annotations;
    this.ret = ret;
    this.name = name;
    this.parameters = parameters;
    this.post_annotations = post_annotations;
    this.body = body;

    assert annotations != null;
    assert name != null;
    assert post_annotations != null;
  }

  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();

    do_append_all(result, annotations);
    if (ret != null) {
      result.append(ret);
    }
    result.append(parameters);
    do_append_all(result, post_annotations);
    if (body != null) {
      result.append(body);
    }

    return result;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, name);
  }
}
