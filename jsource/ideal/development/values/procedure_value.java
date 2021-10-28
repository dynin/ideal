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
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.types.*;
import ideal.development.flavors.*;

public interface procedure_value extends abstract_value, value_wrapper<procedure_value> {
  action_name name();
  // This is needed to resolve abstract_value/value_wrapper ambiguity...
  type type_bound();
  @Nullable declaration get_declaration();
  boolean has_this_argument();
  boolean supports_parameters(action_parameters parameters, action_context context);
  action bind_this_action(action from, origin pos);
  procedure_value bind_this(entity_wrapper this_argument);
  analysis_result bind_parameters(action_parameters params, action_context context, origin pos);
  entity_wrapper execute(entity_wrapper this_argument, readonly_list<entity_wrapper> args,
      execution_context the_execution_context);
}
