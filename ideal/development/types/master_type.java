/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import ideal.library.graphs.*;
import javax.annotation.Nullable;
import ideal.development.annotations.dont_display;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.actions.*;
import ideal.development.kinds.*;

public class master_type extends base_principal_type {
  private final action_name short_name;
  private final @Nullable principal_type parent;
  private final kind the_kind;
  @dont_display
  private type_declaration_context the_context;
  @dont_display
  private dictionary<type_parameters, parametrized_type> parametrized_types;
  parametrized_type primary_type;

  public master_type(kind the_kind, @Nullable flavor_profile the_flavor_profile,
      action_name short_name, principal_type parent, type_declaration_context the_context,
      @Nullable declaration the_declaration) {
    super(the_flavor_profile, declaration_pass.NONE, the_declaration);
    this.short_name = short_name;
    this.the_kind = the_kind;
    this.the_context = the_context;
    this.parent = parent;
  }

  /** Constructor for simple types.  See |core_types|. */
  public master_type(action_name short_name, kind the_kind) {
    super(the_kind.default_profile(), declaration_pass.METHODS_AND_VARIABLES, null);
    this.short_name = short_name;
    this.the_kind = the_kind;
    this.the_context = null;
    this.parent = null;
  }

  @Override
  public @Nullable principal_type get_parent() {
    return parent;
  }

  @Override
  public kind get_kind() {
    return the_kind;
  }

  @Override
  public flavor_profile default_flavor_profile() {
    return the_kind.default_profile();
  }

  @Override
  public action_name short_name() {
    return short_name;
  }

  @Override
  protected type_declaration_context get_context() {
    assert the_context != null;
    return the_context;
  }

  public void set_context(type_declaration_context the_context) {
    assert this.the_context == null;
    this.the_context = the_context;
  }

  private void init_parametrized_map() {
    if (parametrized_types == null) {
      parametrized_types = new hash_dictionary<type_parameters, parametrized_type>();
    }
  }

  public boolean is_parametrizable() {
    return parametrized_types != null;
  }

  public type bind_parameters(type_parameters parameters) {
    init_parametrized_map();
    // TODO: use position.
    parametrized_type result = parametrized_types.get(parameters);
    if (result == null) {
      result = make_parametrized();
      parametrized_types.put(parameters, result);
      result.set_parameters(parameters);
      if (primary_type != null) {
        assert the_context != null;
        graph<principal_type, position> the_type_graph = the_context.type_graph();
        // TODO: do not panic but report a diagnostic
        assert !the_type_graph.introduces_cycle(result, primary_type);
        the_type_graph.add_edge(result, primary_type, semantics.BUILTIN_POSITION);
      } else {
        assert is_special();
      }
    }
    return result;
  }

  private boolean is_special() {
    return the_kind == type_kinds.procedure_kind || the_kind == type_kinds.union_kind;
  }

  private parametrized_type make_parametrized() {
    init_parametrized_map();
    return new parametrized_type(this);
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
    assert parametrized.get_master() == this;
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
  public string describe(type_format format) {
    base_type the_parent = (base_type) parent;
    if (the_parent != null) {
      if (format == type_format.FULL) {
        return new base_string(the_parent.describe(type_format.FULL), ".", short_name.to_string());
      } else if (format == type_format.TWO_PARENTS) {
        return new base_string(the_parent.describe(type_format.ONE_PARENT), ".",
            short_name.to_string());
      } else if (format == type_format.ONE_PARENT) {
        return new base_string(the_parent.describe(type_format.SHORT), ".", short_name.to_string());
      }
    }
    return short_name.to_string();
  }
}
