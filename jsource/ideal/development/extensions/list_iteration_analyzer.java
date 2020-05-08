/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.extensions;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.machine.annotations.dont_display;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.constructs.*;
import ideal.development.extensions.*;
import ideal.development.flavors.*;
import ideal.development.modifiers.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.values.*;
import ideal.development.declarations.*;
import ideal.development.analyzers.*;

public class list_iteration_analyzer extends single_pass_analyzer implements declaration {

  private static final action_name BLOCK_NAME =
      new special_name(new base_string("iterate"), new base_string("list_iteration_analyzer"));

  public final annotation_set annotations;
  public final action_name var_name;
  private final analyzable init;
  @dont_display
  public final analyzable body;
  @dont_display
  protected local_variable element_var;
  @dont_display
  private principal_type loop_block;
  @dont_display
  private @Nullable type element_type;
  public @Nullable action init_action;

  public list_iteration_analyzer(list_iteration_construct source) {
    super(source);
    // TODO: return error_signals if these fail
    assert source.var_decl.type == null;
    assert source.var_decl.init != null;
    assert source.var_decl.annotations.is_empty();

    annotations = analyzer_utilities.LOCAL_MODIFIERS;
    var_name = source.var_decl.name;
    init = make(source.var_decl.init);
    body = make(source.body);
  }

  public list_iteration_analyzer(annotation_set annotations, action_name var_name, analyzable init,
      analyzable body, position source) {
    super(source);
    this.annotations = annotations;
    this.var_name = var_name;
    this.init = init;
    this.body = body;
  }

  @Override
  public principal_type inner_type() {
    assert loop_block != null;
    return loop_block;
  }

  public type get_element_type() {
    assert element_type != null;
    return element_type;
  }

  @Override
  protected analysis_result do_single_pass_analysis() {
    loop_block = make_block(BLOCK_NAME, this);

    @Nullable error_signal error = find_error(init);
    if (error != null) {
      return error;
    }

    init_action = action_utilities.to_value(action_not_error(init), this);
    type should_be_list_type = init_action.result().type_bound();
    element_type = element_type_of_list(should_be_list_type);
    if (element_type == null) {
      return new error_signal(new base_string("List type expected"), init);
    }

    // TODO: when list params are flavored, remove this.
    type var_type = element_type.get_flavored(flavors.mutable_flavor);

    local_variable_declaration decl = new local_variable_declaration(annotations, var_name,
        loop_block, flavors.readonly_flavor, var_type, null, this);

    element_var = decl.get_access();
    get_context().add(loop_block, var_name, element_var.to_action(this));

    error = find_error(init);
    if (error != null) {
      return error;
    }

    error = find_error(body);
    if (error != null) {
      return error;
    }

    return new list_iteration_action(this);
  }

  public action body_action() {
    return action_not_error(body);
  }

  private @Nullable type element_type_of_list(type should_be_list_type) {
    should_be_list_type = should_be_list_type.principal();
    if (! (should_be_list_type instanceof parametrized_type)) {
      return null;
    }
    parametrized_type pt = (parametrized_type) should_be_list_type;
    if (!pt.get_parameters().is_fixed_size()) {
      return null;
    }
    immutable_list<abstract_value> parameters = pt.get_parameters().fixed_size_list();
    if (parameters.size() != 1) {
      return null;
    }
    type param = (type) parameters.first();
    principal_type master = pt.get_master();

    if (master == library().list_type()) {
      return param;
    } else if (library().is_reference_type(pt)) {
      return element_type_of_list(param);
    } else {
      return null;
    }
  }
}
