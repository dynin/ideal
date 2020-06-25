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

public class list_iteration_analyzer extends extension_analyzer implements declaration {

  private static final action_name BLOCK_NAME =
      new special_name(new base_string("iterate"), new base_string("list_iteration_analyzer"));
  private static simple_name LIST_NAME = simple_name.make("list");
  private static simple_name INDEX_NAME = simple_name.make("index");
  private static simple_name GET_NAME = simple_name.make("get");

  public final annotation_set annotations;
  public final action_name var_name;
  private final analyzable var_type;
  public final analyzable init;
  @dont_display
  public final analyzable body;
  @dont_display
  private principal_type loop_block;
  @dont_display
  private @Nullable type element_type;
  public @Nullable action init_action;

  public list_iteration_analyzer(list_iteration_construct source) {
    super(source);
    // TODO: return error_signals if these fail
    assert source.var_decl.annotations.is_empty();

    annotations = analyzer_utilities.PRIVATE_MODIFIERS;
    var_type = (source.var_decl.type != null) ? make(source.var_decl.type) : null;
    var_name = source.var_decl.name;
    init = make(source.var_decl.init);
    body = make(source.body);
  }

  public list_iteration_analyzer(annotation_set annotations, action_name var_name, analyzable init,
      analyzable body, origin source) {
    super(source);
    this.annotations = annotations;
    this.var_type = null;
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
  public analyzable do_expand() {
    // TODO: support specifying element type
    if (var_type != null) {
      return new error_signal(new base_string("Element type not expected, it is inferred"),
          var_type);
    }

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

    return rewrite_as_for_loop();
  }

  private analyzable rewrite_as_for_loop() {
    origin the_origin = this;
    common_library library = common_library.get_instance();

    if (! (var_name instanceof simple_name)) {
      return new error_signal(new base_string("Simple name expected"), this);
    }
    simple_name element_name = (simple_name) var_name;
    simple_name list_name = name_utilities.join(element_name, LIST_NAME);
    simple_name index_name = name_utilities.join(element_name, INDEX_NAME);

    type list_type = library().list_type().bind_parameters(
        new type_parameters(new base_list<abstract_value>(element_type))).get_flavored(
        flavor.readonly_flavor);

    local_variable_declaration list_declaration =
        new local_variable_declaration(analyzer_utilities.PRIVATE_FINAL_MODIFIERS, list_name,
        flavor.immutable_flavor, list_type, new analyzable_action(init_action), the_origin);

    local_variable_declaration index_declaration = new local_variable_declaration(
        analyzer_utilities.PRIVATE_MODIFIERS, index_name,
        flavor.mutable_flavor, library.immutable_nonnegative_type(), new analyzable_action(
        new integer_value(0, library.immutable_nonnegative_type()).to_action(the_origin)),
        the_origin);

    analyzable index_condition = new parameter_analyzer(
        new resolve_analyzer(operator.LESS, the_origin),
        new base_list<analyzable>(
          new analyzable_action(index_declaration.dereference_access()),
          new resolve_analyzer(
            new analyzable_action(list_declaration.dereference_access()),
            common_library.size_name,
            the_origin
          )
        ),
        the_origin
      );

    analyzable index_increment = new parameter_analyzer(
        new resolve_analyzer(operator.ADD_ASSIGN, the_origin),
        new base_list<analyzable>(
          new analyzable_action(index_declaration.get_access()),
          new analyzable_action(
            new integer_value(1, library.immutable_nonnegative_type()).to_action(the_origin)
          )
        ),
        the_origin
      );

    analyzable element_get = new parameter_analyzer(
        //new analyzable_action(list_declaration.dereference_access()),
        // TODO: list_declaration.get_access() should work.
        new resolve_analyzer(list_name, the_origin),
        new base_list<analyzable>(
            new analyzable_action(index_declaration.dereference_access())
        ),
        the_origin
      );

    local_variable_declaration element_declaration = new local_variable_declaration(
        annotations, element_name, flavor.immutable_flavor, element_type, element_get, the_origin);

    list<analyzable> body_list = new base_list<analyzable>(element_declaration);
    if (body instanceof block_analyzer) {
      body_list.append(((block_analyzer) body).get_body());
    } else {
      body_list.append(body);
    }
    statement_list_analyzer body_statements = new statement_list_analyzer(body_list, the_origin);

    block_analyzer body_block = new block_analyzer(body_statements, the_origin);

    for_analyzer for_statement = new for_analyzer(
        index_declaration,
        index_condition,
        index_increment,
        body_block,
        the_origin);

    return new block_analyzer(
        new statement_list_analyzer(
            new base_list<analyzable>(list_declaration, for_statement),
            the_origin
        ),
        the_origin);
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
