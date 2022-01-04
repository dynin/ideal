/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.functions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.actions.*;
import ideal.development.notifications.*;
import ideal.development.declarations.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.values.*;

public class type_union_op extends binary_procedure {

  public type_union_op() {
    super(operator.GENERAL_OR, true,
        common_types.value_type().get_flavored(flavor.any_flavor),
        common_types.any_type(),
        common_types.any_type());
  }

  @Override
  protected analysis_result bind_binary(action first, action second, action_context context,
      origin the_origin) {

    principal_type null_type = common_types.null_type();
    action primary_action;

    if (first.result() == null_type) {
      primary_action = second;
    } else if (second.result() == null_type) {
      primary_action = first;
    } else {
      return new error_signal(new base_string("Can't find null parameter"), the_origin);
    }

    if (! (primary_action.result() instanceof type)) {
      return new error_signal(new base_string("Principal type expected"), primary_action);
    }

    list<abstract_value> union_parameters = new base_list<abstract_value>();
    union_parameters.append(primary_action.result());
    union_parameters.append(null_type.get_flavored(flavor.deeply_immutable_flavor));

    return type_utilities.make_union(union_parameters).to_action(the_origin);
  }

  @Override
  protected entity_wrapper execute_binary(entity_wrapper first, entity_wrapper second,
      execution_context the_execution_context) {

    utilities.panic("Unimplemented type_union_op.execute_binary()");
    return null;
  }
}
