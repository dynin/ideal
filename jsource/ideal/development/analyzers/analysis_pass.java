/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;

// TODO(m): make identifier
// TODO(m): has strong ordering
public enum analysis_pass implements deeply_immutable_data, stringable {

  BEFORE_EVALUATION,
  TARGET_DECL,
  TYPE_DECL,
  IMPORT_AND_TYPE_VAR_DECL,
  SUPERTYPE_DECL,
  PREPARE_METHOD_AND_VARIABLE,
  METHOD_AND_VARIABLE_DECL,
  BODY_CHECK;

  public boolean is_before(analysis_pass other) {
    return this.ordinal() < other.ordinal();
  }

  public boolean is_after(analysis_pass other) {
    return this.ordinal() > other.ordinal();
  }

  public static immutable_list<analysis_pass> all() {
    list<analysis_pass> result = new base_list<analysis_pass>();
    for (analysis_pass pass : values()) {
      result.append(pass);
    }
    return result.frozen_copy();
  }

  public static analysis_pass last() {
    return values()[values().length - 1];
  }

  @Override
  public string to_string() {
    return new base_string(name(), "/", String.valueOf(ordinal()));
  }
}
