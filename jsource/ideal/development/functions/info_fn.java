/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.functions;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.library.reflections.*;
import ideal.library.messages.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.analyzers.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;

public class info_fn extends base_procedure {

  public info_fn(action_name the_name) {
    super(the_name, common_types.make_procedure(false, common_types.immutable_void_type()));
  }

  @Override
  protected boolean is_valid_procedure_arity(Integer arity) {
    return true;
  }

  @Override
  protected final type get_argument_type(Integer index) {
    return string_helper.readonly_stringable();
  }

  @Override
  public entity_wrapper execute(entity_wrapper this_argument, readonly_list<entity_wrapper> args,
      execution_context context) {
    assert this_argument instanceof null_wrapper;
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < args.size(); ++i) {
      entity_wrapper arg = args.get(i);
      string as_string = string_helper.to_string(arg, context);
      out.append(utilities.s(as_string));
    }

    string line = new base_string(out.toString());
    log_message the_message = new simple_message(log_level.INFORMATIONAL, line);
    log.log_output.write(the_message);

    return common_values.void_instance();
  }

  @Override
  public declaration get_declaration() {
    return builtin_declaration.instance;
  }
}
