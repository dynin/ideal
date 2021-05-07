/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import ideal.library.graphs.*;
import javax.annotation.Nullable;
import ideal.machine.annotations.dont_display;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.kinds.*;

public class parametrizable_state extends debuggable {
  private final master_type master;
  @dont_display private dictionary<type_parameters, parametrized_type> parametrized_types;
  private @Nullable parametrized_type primary_type;

  public parametrizable_state(master_type master) {
    this.master = master;
    parametrized_types = new hash_dictionary<type_parameters, parametrized_type>();
  }

  public master_type get_master() {
    return master;
  }

  public type bind_parameters(type_parameters parameters) {
    // TODO: use origin.
    parametrized_type result = parametrized_types.get(parameters);
    if (result == null) {
      result = make_parametrized();
      parametrized_types.put(parameters, result);
      result.set_parameters(parameters);
      if (primary_type != null) {
        graph<principal_type, origin> the_type_graph = master.get_context().type_graph();
        // TODO: do not panic but report a diagnostic
        assert !the_type_graph.introduces_cycle(result, primary_type);
        the_type_graph.add_edge(result, primary_type, type_utilities.PRIMARY_TYPE_ORIGIN);
      } else {
        assert is_special();
      }
    }
    return result;
  }

  private boolean is_special() {
    kind the_kind = master.get_kind();
    return the_kind == type_kinds.procedure_kind || the_kind == type_kinds.union_kind;
  }

  private parametrized_type make_parametrized() {
    return new parametrized_type(master);
  }

  public @Nullable parametrized_type get_primary() {
    return primary_type;
  }

  public parametrized_type make_primary() {
    assert primary_type == null;
    primary_type = make_parametrized();
    return primary_type;
  }

  public @Nullable parametrized_type lookup_parametrized(type_parameters parameters) {
    return parametrized_types.get(parameters);
  }

  public void bind_parametrized(parametrized_type parametrized, type_parameters parameters) {
    assert parametrized.get_master() == this.master;
    assert !parametrized.parameters_defined();
    assert is_special() || primary_type == parametrized;
    if (parametrized_types.contains_key(parameters)) {
      utilities.panic("Already defined param type " + parametrized + " for " + parameters);
    }
    assert !parametrized_types.contains_key(parameters);

    parametrized.set_parameters(parameters);
    parametrized_types.put(parameters, parametrized);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, master);
  }
}
