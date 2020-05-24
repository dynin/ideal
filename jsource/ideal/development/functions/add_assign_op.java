/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.functions;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;
import ideal.development.values.*;

// TODO: handle (possibly negative) integer types
public class add_assign_op extends binary_procedure {

  public add_assign_op() {
    super(operator.ADD_ASSIGN, false,
        library().immutable_nonnegative_type(),
        library().get_reference(flavor.mutable_flavor, library().immutable_nonnegative_type()),
        library().immutable_nonnegative_type());
  }

  @Override
  protected entity_wrapper execute_binary(entity_wrapper first, entity_wrapper second,
      execution_context the_execution_context) {

    reference_wrapper ref = (reference_wrapper) first;

    integer_value lhs = (integer_value) ref.get();
    integer_value rhs = (integer_value) second;

    integer_value result = new integer_value(lhs.unwrap() + rhs.unwrap(),
        library().immutable_nonnegative_type());
    ref.set(result);

    return result;
  }
}
