/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.functions;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.runtime.texts.markup_formatter;
import ideal.runtime.reflections.*;
import ideal.machine.elements.runtime_util;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;
import ideal.development.values.*;

public class escape_fn extends base_procedure {

  public escape_fn(action_name name) {
    super(name, common_types.make_procedure(true, common_types.immutable_string_type(),
        common_types.immutable_string_type()));
  }

  @Override
  public final entity_wrapper execute(entity_wrapper this_argument,
          readonly_list<entity_wrapper> args, execution_context exec_context) {
    assert this_argument instanceof null_wrapper;
    assert args.size() == 1;
    string s = ((string_value) args.first()).unwrap();
    s = runtime_util.escape_markup(s);
    return new base_string_value(s, common_types.immutable_string_type());
  }
}
