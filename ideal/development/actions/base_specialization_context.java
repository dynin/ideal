/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.declarations.*;
import ideal.development.types.*;

public class base_specialization_context extends debuggable implements specialization_context {

  private immutable_dictionary<master_type, abstract_value> params;

  public base_specialization_context(readonly_dictionary<master_type, abstract_value> params) {
    this.params = params.frozen_copy();
  }

  public base_specialization_context(master_type parameter, abstract_value value) {
    this(new list_dictionary<master_type, abstract_value>(parameter, value));
  }

  @Override
  public @Nullable abstract_value lookup(principal_type key) {
    if (key instanceof master_type) {
      return params.get((master_type) key);
    } else {
      return null;
    }
  }

  public immutable_dictionary<master_type, abstract_value> params() {
    return params;
  }

  @Override
  public string to_string() {
    StringBuilder s = new StringBuilder();
    boolean first = true;
    s.append('[');

    if (!params.is_empty()) {
      readonly_list<dictionary.entry<master_type, abstract_value>> named_list =
          params.elements();
      for (int i = 0; i < named_list.size(); ++i) {
        if (first) {
          first = false;
        } else {
          s.append(", ");
        }
        s.append(utilities.s(named_list.get(i).key().short_name().to_string()));
        s.append(": ");
        s.append(utilities.s(named_list.get(i).value().to_string()));
      }
    }

    s.append(']');
    return new base_string(s.toString());
  }
}
