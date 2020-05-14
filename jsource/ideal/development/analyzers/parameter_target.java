/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.actions.*;

public class parameter_target extends debuggable implements action_target {
  public final action_parameters parameters;
  private final analysis_context the_context;
  private type procedure_type;

  public parameter_target(action_parameters parameters, analysis_context the_context) {
    this.parameters = parameters;
    this.the_context = the_context;
  }

  public int arity() {
    return parameters.arity();
  }

  public type to_procedure_type() {
    if (procedure_type == null) {
      common_library library = common_library.get_instance();
      list<abstract_value> parameter_list = new base_list<abstract_value>();
      parameter_list.append(library.entity_type());
      parameter_list.append_all(parameters.to_value_list());
      procedure_type = library.procedure_type().bind_parameters(
          new type_parameters(parameter_list)).get_flavored(flavor.DEFAULT_FLAVOR);
    }
    return procedure_type;
  }

  @Override
  public boolean matches(abstract_value the_abstract_value) {
    return analyzer_utilities.is_parametrizable(the_abstract_value, parameters.to_type_parameters(),
        the_context);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, parameters.to_value_string());
  }
}
