/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.values;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.declarations.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.actions.*;

public class list_context implements variable_context {
  private final readonly_list the_list;

  public list_context(readonly_list the_list) {
    this.the_list = the_list;
  }

  @Override
  public void put_var(variable_id key, value_wrapper value) {
    utilities.panic("list_context.put_var() for " + key);
  }

  @Override
  public value_wrapper get_var(variable_id key) {
    if (key.short_name() == common_names.size_name) {
      return new integer_value(the_list.size(),
          common_library.get_instance().immutable_nonnegative_type());
    }

    utilities.panic("list_context.get_var() for " + key);
    return null;
  }
}
