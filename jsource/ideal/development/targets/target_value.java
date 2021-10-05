/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.targets;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.values.*;

public abstract class target_value extends base_procedure {

  public target_value(simple_name the_name) {
    super(the_name, common_types.make_procedure(true, elementary_types.target_type()));
  }

  @Override
  protected boolean is_valid_procedure_arity(int arity) {
    return true;
  }

  @Override
  protected final type get_argument_type(int index) {
    return elementary_types.any_type();
  }

  @Override
  public entity_wrapper execute(entity_wrapper this_argument, readonly_list<entity_wrapper> args,
      execution_context the_execution_context) {
    utilities.panic("Can't execute a target_value");
    return null;
  }

  public abstract void process(action_parameters parameters, analysis_context the_context);
}
