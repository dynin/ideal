/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.values.*;
import ideal.development.modifiers.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;

public class overloaded_procedure extends base_procedure {
  private final list<procedure_executor> procedures;

  public overloaded_procedure(procedure_executor the_executor) {
    super(the_executor.name(), the_executor.type_bound());
    procedures = new base_list<procedure_executor>(the_executor);
  }

  readonly_list<procedure_executor> procedures() {
    return procedures;
  }

  public void add(procedure_executor the_executor) {
    procedures.append(the_executor);
  }

  private procedure_executor first() {
    return procedures.first();
  }

  @Override
  public boolean has_this_argument() {
    return get_category() != procedure_category.STATIC;
  }

  @Override
  public declaration get_declaration() {
    return first().get_declaration();
  }

  public procedure_category get_category() {
    return first().get_category();
  }

  @Override
  public boolean is_parametrizable(action_parameters parameters, analysis_context context) {
    int matches = 0;
    for (int i = 0; i < procedures.size(); ++i) {
      if (procedures.get(i).is_parametrizable(parameters, context)) {
        ++matches;
      }
    }
    return matches == 1;
  }

  @Override
  public analysis_result bind_parameters(action_parameters parameters, analysis_context context,
      origin the_origin) {
    // TODO: make the assertion optional
    assert is_parametrizable(parameters, context);
    for (int i = 0; i < procedures.size(); ++i) {
      if (procedures.get(i).is_parametrizable(parameters, context)) {
        return procedures.get(i).bind_parameters(parameters, context, the_origin);
      }
    }

    utilities.panic("Can't bind parameters");
    return null;
  }

  @Override
  public entity_wrapper execute(readonly_list<entity_wrapper> arguments,
      execution_context the_context) {
    return first().execute(arguments, the_context);
  }

  public string to_string() {
    return utilities.describe(this, new base_string(name() + " #" + procedures.size()));
  }
}
