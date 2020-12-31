/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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

public class concatenate_assign_op extends binary_procedure {

  public concatenate_assign_op() {
    super(operator.CONCATENATE_ASSIGN, false,
        library().immutable_string_type(),
        library().get_reference(flavor.mutable_flavor, library().immutable_string_type()),
        library().immutable_string_type());
  }

  @Override
  protected entity_wrapper execute_binary(entity_wrapper first, entity_wrapper second,
      execution_context the_execution_context) {

    reference_wrapper ref = (reference_wrapper) first;

    string_value s1 = (string_value) ref.get();
    string_value s2 = (string_value) second;

    string_value result = new base_string_value(new base_string(s1.unwrap(), s2.unwrap()),
        library().immutable_string_type());
    ref.set(result);

    return result;
  }
}
