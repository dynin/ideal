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
    super(name, procedure_util.make_procedure_type(true, library().immutable_string_type(),
        library().immutable_string_type()));
  }

  @Override
  public final entity_wrapper execute(readonly_list<entity_wrapper> args,
          execution_context exec_context) {
    assert args.size() == 1;
    string s = ((string_value) args.get(0)).unwrap();
    s = runtime_util.escape_markup(s);
    return new base_string_value(s, library().immutable_string_type());
  }
}
