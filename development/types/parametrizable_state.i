-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class parametrizable_state {
  extends debuggable;

  private master_type master;
  private dont_display dictionary[type_parameters, parametrized_type] parametrized_types;
  private var parametrized_type or null primary_type;

  parametrizable_state(master_type master) {
    this.master = master;
    this.parametrized_types = hash_dictionary[type_parameters, parametrized_type].new();
  }

  master_type get_master => master;

  type bind_parameters(type_parameters parameters) {
    -- TODO: use origin.
    var result : parametrized_types.get(parameters);
    if (result is null) {
      result = make_parametrized();
      parametrized_types.put(parameters, result);
      result.set_parameters(parameters);
      if (primary_type is_not null) {
        the_type_graph : master.get_context().type_graph;
        -- TODO: the cast is redundant
        ptype : primary_type;
        assert ptype is_not null;
        -- TODO: do not panic but report a diagnostic
        assert !the_type_graph.introduces_cycle(result, ptype);
        the_type_graph.add_edge(result, ptype, type_utilities.PRIMARY_TYPE_ORIGIN);
      } else {
        assert is_special();
      }
    }
    return result;
  }

  private boolean is_special => master.get_kind == type_kinds.#id:procedure_kind;

  private parametrized_type make_parametrized() => parametrized_type.new(master);

  parametrized_type or null get_primary => primary_type;

  parametrized_type make_primary() {
    assert primary_type is null;
    result : make_parametrized();
    primary_type = result;
    return result;
  }

  parametrized_type or null lookup_parametrized(type_parameters parameters) {
    return parametrized_types.get(parameters);
  }

  void bind_parametrized(parametrized_type parametrized, type_parameters parameters) {
    assert parametrized.get_master() == this.master;
    assert !parametrized.parameters_defined();
    assert is_special() || primary_type == parametrized;
    if (parametrized_types.contains_key(parameters)) {
      utilities.panic("Already defined param type " ++ parametrized ++ " for " ++ parameters);
    }

    parametrized.set_parameters(parameters);
    parametrized_types.put(parameters, parametrized);
  }

  implement string to_string => utilities.describe(this, master);
}
