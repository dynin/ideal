/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import javax.annotation.Nullable;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class supertype_construct extends base_construct {
  public final readonly_list<annotation_construct> annotations;
  public final @Nullable type_flavor subtype_flavor;
  public final subtype_tag tag;
  public final readonly_list<construct> types;

  public supertype_construct(readonly_list<annotation_construct> annotations,
      type_flavor subtype_flavor,
      subtype_tag tag,
      readonly_list<construct> types,
      position pos) {
    super(pos);
    this.annotations = annotations;
    this.subtype_flavor = subtype_flavor;
    this.tag = tag;
    this.types = types;

    // TODO: handle annotations.
    assert annotations.is_empty();
    assert !types.is_empty();
  }

  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();

    do_append_all(result, annotations);
    result.append_all(types);

    return result;
  }
}
