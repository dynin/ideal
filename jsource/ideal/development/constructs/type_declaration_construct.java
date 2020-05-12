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

public class type_declaration_construct extends base_construct {
  public final readonly_list<annotation_construct> annotations;
  public final kind kind;
  public final action_name name;
  public final @Nullable list_construct parameters;
  public final readonly_list<construct> body;
  public type_declaration_construct(readonly_list<annotation_construct> annotations,
                                    kind kind,
                                    action_name name,
                                    @Nullable list_construct parameters,
                                    readonly_list<construct> body,
                                    origin pos) {
    super(pos);
    this.annotations = annotations;
    this.kind = kind;
    this.name = name;
    this.parameters = parameters;
    this.body = body;

    // TODO: we should signal error instead.
    assert parameters == null || parameters.elements.is_not_empty();

    assert body != null;
  }

  public boolean has_parameters() {
    return parameters != null;
  }

  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();

    do_append_all(result, annotations);
    if (parameters != null) {
      result.append(parameters);
    }
    if (body != null) {
      result.append_all(body);
    }

    return result;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, name);
  }
}
