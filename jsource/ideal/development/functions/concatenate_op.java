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

public class concatenate_op extends binary_procedure {

  public concatenate_op() {
    super(operator.CONCATENATE, true,
        library().immutable_string_type(),
        string_helper.readonly_stringable(),
        string_helper.readonly_stringable());
  }

  @Override
  protected entity_wrapper execute_binary(entity_wrapper first, entity_wrapper second,
        execution_context the_execution_context) {

    StringBuilder result = new StringBuilder();

    result.append(utilities.s(string_helper.to_string(first, the_execution_context)));
    result.append(utilities.s(string_helper.to_string(second, the_execution_context)));

    return new base_string_value(new base_string(result.toString()),
        library().immutable_string_type());
  }
}
